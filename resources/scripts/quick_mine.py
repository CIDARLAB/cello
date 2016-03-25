#!/usr/bin/env python
"""
	quick.py

 	Usage:
    ------
    python quick.py -input "p.gray p.lightblue i.lightred r.green c.orange t.purple -t.black -c.yellow -p.yellow" -output out.pdf -clean true
    allowed part types: 
     p: promoter   i: ribozyme   r: rbs   c: cds   t: terminator   s: spacer   =: scar

    reverse part direction by using '-' before the 1-letter part type

    allowed colors
     black,gray,red,orange,yellow,green,blue,purple,lightred,lightorange,lightyellow,lightgreen,lightblue,lightpurple

"""
#    Plot SBOL Designs
#    Copyright (C) 2014 by
#    Thomas E. Gorochowski <tom@chofski.co.uk>
#    Bryan Der <bder@mit.edu>
#    All rights reserved.
#    OSI Non-Profit Open Software License ("Non-Profit OSL") 3.0 license.

from argparse import ArgumentParser
import os.path
import os
import re


def get_rgb():
	colors = {}
	colors['black']  = '0.00;0.00;0.00'
	colors['gray']   = '0.60;0.60;0.60'
	colors['red']    = '0.89;0.10;0.11'
	colors['orange'] = '1.00;0.50;0.00'
	colors['yellow'] = '1.00;1.00;0.00'
	colors['green']  = '0.20;0.63;0.17'
	colors['blue']   = '0.12;0.47;0.71'
	colors['purple'] = '0.42;0.24;0.60'
	colors['lightred']    = '0.98;0.60;0.60'
	colors['lightorange'] = '0.99;0.75;0.44'
	colors['lightyellow'] = '1.00;1.00;0.60'
	colors['lightgreen']  = '0.70;0.87;0.54'
	colors['lightblue']   = '0.65;0.81;0.89'
	colors['lightpurple'] = '0.79;0.70;0.84'
	#for c in colors:
	#	print colors[c], c
	return colors

def get_part_types():
	types = {}
	types['p'] = 'Promoter'
	types['i'] = 'Ribozyme'
	types['r'] = 'RBS'
	types['c'] = 'CDS'
	types['t'] = 'Terminator'
	types['s'] = 'Spacer'
	types['='] = 'Scar'
	return types

def get_plot_param_defaults():
	params = ""
	params += "parameter,value\n"
	params += "linewidth,1\n"
	params += "scale,1\n"
	params += "fig_x,8.5\n"
	params += "fig_y,1\n"
	params += "show_title,N\n"
	params += "backbone_pad_left,3\n"
	params += "backbone_pad_right,3\n"
	params += "axis_y,15\n"
	return params

def process_arguments (input):

	#parts = nltk.word_tokenize(input)
	whitesp = re.compile(' ')
	parts = whitesp.split(input)

	colors = get_rgb()
	part_list = []

	for part in parts:
		if(part == ""):
			continue

		pattern = re.compile('\.')
		part_info = pattern.split(part)
		

		# forward cds: c.blue
		# reverse cds: -c.blue
		part_direction = "+"
		part_type = part_info[0][0] #first character of first split on .

		if(part_type[0] == '-'): #reverse
			part_type  = part_info[0][1] #second character of first split on .
			part_direction = "-"

		part_color = part_info[1]
		part_rgb   = colors[part_color]
		part_list.append( [part_type, part_rgb, part_direction] )

	return part_list
		#print 'partname', part_name,'partcolor',colors[part_color]
	
def write_files (part_list):

	types = get_part_types()

	file_plotparams = open('plot_parameters.csv', 'w')
	file_partinfo   = open('part_information.csv', 'w')
	file_dnadesign  = open('dna_designs.csv', 'w')
	
	file_plotparams.write(get_plot_param_defaults())
	file_partinfo.write("part_name,type,x_extent,y_extent,start_pad,end_pad,color,hatch,arrowhead_height,arrowhead_length,linestyle,linewidth\n")
	file_dnadesign.write("design_name,parts,\n")

	dnadesign_string = "Design 1: Quick,"

	num_parts = len(part_list)
	for i in range(num_parts):
		#print part_list[i],i
		type = types[part_list[i][0]]
		color =      part_list[i][1]
		direction =  part_list[i][2]
		name = type + str(i) #looks like: p1,i2,r3,c4,t5

		part_info_line = name + "," + type + ",,,,," + color + ",,,,,"

		#part_information name does not start with r if reverse
		file_partinfo.write(part_info_line+'\n')

		#dna_design name starts with r if reverse
		if(direction == '-'):
			name = "r" + name
		
		dnadesign_string += name + ","
		

	file_dnadesign.write(dnadesign_string)



def main():
	#print __doc__

	parser = ArgumentParser(description="one line quick plot")
	parser.add_argument("-input",  dest="input",  required=True,  help="\"p.gray p.lightblue i.lightred r.green c.orange t.purple -t.black -c.yellow -p.yellow\"", metavar="string")
	parser.add_argument("-output", dest="output", required=False, help="output pdf filename")
	parser.add_argument("-clean",  dest="clean",  required=False, help="true/false: remove csv generated files")
	#parser.add_argument("-reg",    dest="reg",    required=False, help="infer regulation arcs from colors")
	args = parser.parse_args()

	######################################################################
	#  process arguments
	######################################################################
	#input
	part_list = process_arguments(args.input)
	#output
	output_pdf = "out.pdf"
	if(args.output):
		out_pdf = args.output

	
	######################################################################
	#  write csvs
	######################################################################
	write_files(part_list)


	######################################################################
	#  make figure
	######################################################################
	# [TEG] Commented this out as we shouldn't use paths (instead add python to $PATH)
	#command = "~/anaconda/bin/python -W ignore " + os.path.dirname(__file__) + "/plot_SBOL_designs.py"
	command = "~/anaconda/bin/python -W ignore " + "/Users/peng/MIT-BroadFoundry/dnaplotlib/plot_SBOL_designs.py"
	options = ""
	options += " -params plot_parameters.csv"
	options += " -parts part_information.csv"
	options += " -designs dna_designs.csv "
	options += " -output " + out_pdf
	#options += " -regulation reg_information.csv"
	print 'writing ' + out_pdf
	os.system(command + options)


	######################################################################
	#  remove csvs
	######################################################################
	if(args.clean):
		if(os.path.isfile('plot_parameters.csv')):
			os.remove('plot_parameters.csv')
		if(os.path.isfile('part_information.csv')):
			os.remove('part_information.csv')
		if(os.path.isfile('dna_designs.csv')):
			os.remove('dna_designs.csv')
		if(os.path.isfile('reg_information.csv')):
			os.remove('reg_information.csv')



if __name__ == "__main__":
 	main()
