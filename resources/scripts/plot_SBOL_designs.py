#!/usr/bin/env python
"""
    plot_SBOL_designs.py

    Plot the design of DNA constructs using SBOL notation.

    Usage:
    ------
    python plot_SBOL_designs.py  -params     PARAM_FILENAME 
                                 -parts      PART_FILENAME 
                                 -designs    DESIGN_FILENAME 
                                [-regulation REG_FILENAME]
                                 -output     OUT_FILENAME
"""
#    Plot SBOL Designs
#    Copyright (C) 2014 by
#    Thomas E. Gorochowski <tom@chofski.co.uk>
#    Bryan Der <bder@mit.edu>
#    All rights reserved.
#    OSI Non-Profit Open Software License ("Non-Profit OSL") 3.0 license.

# Set the backend to use (important for headless servers)
import matplotlib
matplotlib.use('Agg')

import csv
import dnaplotlib as dpl
import matplotlib.pyplot as plt
from argparse import ArgumentParser
import os.path

__author__  = 'Thomas E. Gorochowski <tom@chofski.co.uk>, Voigt Lab, MIT\n\
               Bryan Der <bder@mit.edu>, Voigt Lab, MIT'
__license__ = 'OSI Non-Profit OSL 3.0'
__version__ = '1.0'

def make_float_if_needed (s):
	try:
		float(s)
		return float(s)
	except ValueError:
		return s


def load_plot_parameters (filename):
	plot_params = {}
	param_reader = csv.reader(open(filename, 'rU'), delimiter=',')
	# Ignore header
	header = next(param_reader)
	# Process all parameters
	for row in param_reader:
		if len(row) >= 2:
			if row[1] != '':
				plot_params[row[0]] = make_float_if_needed(row[1])
	return plot_params


def load_part_information (filename):
	part_info = {}
	parts_reader = csv.reader(open(filename, 'rU'), delimiter=',')
	header = next(parts_reader)
	header_map = {}
	for i in range(len(header)):
		header_map[header[i]] = i
	attrib_keys = [k for k in header_map.keys() if k not in ['part_name', 'type']]
	for row in parts_reader:
		# Make the attributes map
		part_attribs_map = {}
		for k in attrib_keys:
			if row[header_map[k]] != '':
				if k == 'color' or k == 'label_color':
					part_attribs_map[k] = [float(x) for x in row[header_map[k]].split(';')]
				else:
					part_attribs_map[k] = make_float_if_needed(row[header_map[k]])
		part_name = row[header_map['part_name']]
		part_type = row[header_map['type']]
		part_info[part_name] = [part_name, part_type, part_attribs_map]
	return part_info


def load_dna_designs (filename, part_info):
	dna_designs = {}
	design_reader = csv.reader(open(filename, 'rU'), delimiter=',')
	# Ignore header
	header = next(design_reader)
	# Process all parameters
	for row in design_reader:
		if len(row[0]) != '':
			part_list = []
			for i in range(1,len(row)):
				# Handle reverse parts
				fwd = True
				part_name = row[i]
				if len(part_name) != 0:
					if part_name[0] == '-':
						part_name = part_name[1:]
						fwd = False
					# Store the design
					part_design = {}
					cur_part_info = part_info[part_name]
					part_design['type'] = cur_part_info[1]
					part_design['name'] = part_name #needed to add part name for regulation
					part_design['fwd']  = fwd       #needed to add fwd for regulation
					if fwd == True:
						part_design['start'] = i
						part_design['end'] = i+1
					else:
						part_design['end'] = i
						part_design['start'] = i+1
					part_design['opts'] = cur_part_info[2]
					part_list.append(part_design)
			dna_designs[row[0]] = part_list
	return dna_designs


def load_regulatory_information (filename, part_info, dna_designs):
	regs_info = {}
	
	reg_reader = csv.reader(open(filename, 'rU'), delimiter=',')
	# Ignore header
	header = next(reg_reader)
	header_map = {}
	for i in range(len(header)):
		header_map[header[i]] = i
	attrib_keys = [k for k in header_map.keys() if k not in ['from_partname', 'type', 'to_partname']]
	
	#reg_reader can only be read once?
	rows = []
	for row in reg_reader:
		rows.append(row)

	design_list = sorted(dna_designs.keys())
	num_of_designs = len(design_list)

	#outer loop: for each design
	for i in range(num_of_designs):
		regs_info[i]=[]
		design =  dna_designs[design_list[i]]

		#middle loop: for each regulation
		for row in rows:
			
			#opts
			reg_attribs_map = {}
			for k in attrib_keys:
				if row[header_map[k]] != '':
					if k == 'color':
						reg_attribs_map[k] = [float(x) for x in row[header_map[k]].split(';')]
					else:
						reg_attribs_map[k] = make_float_if_needed(row[header_map[k]])

			#from, type, to
			type = row[header_map['type']]
			from_partname = row[header_map['from_partname']]
			to_partname   = row[header_map['to_partname']]
			from_part = None;
			to_part = None;
			
			#inner loop: loop through parts to find 'from' and 'to' parts
			for part1 in design: #loop through once to find the cds
				if(part1['name'] == from_partname):
					start_part = part1
					for part2 in design: #loop through again to find the promoter
						if(part2['name'] == to_partname):
							end_part = part2
							#found from-to, save regulation arc
							reg_info = {}
							reg_info['from_part'] = start_part
							reg_info['type'] = row[header_map['type']]
							reg_info['to_part'] = end_part
							reg_info['opts'] = reg_attribs_map
							regs_info[i].append(reg_info)
	return regs_info


def plot_dna (dna_designs, out_filename, plot_params, regs_info):
	# Create the renderer
	if 'axis_y' not in plot_params.keys():
		plot_params['axis_y'] = 35
	left_pad = 0.0
	right_pad = 0.0
	scale = 1.0
	linewidth = 1.0
	fig_y = 5.0
	fig_x = 5.0
	if 'backbone_pad_left' in plot_params.keys():
		left_pad = plot_params['backbone_pad_left']
	if 'backbone_pad_right' in plot_params.keys():
		right_pad = plot_params['backbone_pad_right']
	if 'scale' in plot_params.keys():
		scale = plot_params['scale']
	if 'linewidth' in plot_params.keys():
		linewidth = plot_params['linewidth']
	if 'fig_y' in plot_params.keys():
		fig_y = plot_params['fig_y']
	if 'fig_x' in plot_params.keys():
		fig_x = plot_params['fig_x']
	dr = dpl.DNARenderer(scale=scale, linewidth=linewidth,
		                 backbone_pad_left=left_pad, 
		                 backbone_pad_right=right_pad)

	# We default to the standard regulation renderers
	reg_renderers = dr.std_reg_renderers()
	# We default to the SBOL part renderers
	part_renderers = dr.SBOL_part_renderers()

    # Create the figure
	fig = plt.figure(figsize=(fig_x,fig_y))
	#plt.subplots_adjust(hspace=0.01, wspace=0.01, left=0.05, right=0.95, top=0.99, bottom=0.01)


	# Cycle through the designs an plot on individual axes
	design_list = sorted(dna_designs.keys())
	if(regs_info != None):
		regs_list   = sorted(regs_info.keys())
	
	num_of_designs = len(design_list)
	ax_list = []
	max_dna_len = 0.0
	for i in range(num_of_designs):
		# Create axis for the design and plot
		regs = None
		if(regs_info != None):
			regs   =  regs_info[i]
		design =  dna_designs[design_list[i]]

		ax = fig.add_subplot(num_of_designs,1,i+1)
		if 'show_title' in plot_params.keys() and plot_params['show_title'] == 'Y':
			ax.set_title(design_list[i], fontsize=8)
		start, end = dr.renderDNA(ax, design, part_renderers, regs, reg_renderers)

		dna_len = end-start
		if max_dna_len < dna_len:
			max_dna_len = dna_len
		ax_list.append(ax)
	for ax in ax_list:
		ax.set_xticks([])
		ax.set_yticks([])
		# Set bounds
		ax.set_xlim([(-0.01*max_dna_len)-left_pad,
			        max_dna_len+(0.01*max_dna_len)+right_pad])
		ax.set_ylim([-plot_params['axis_y'],plot_params['axis_y']])
		ax.set_aspect('equal')
		ax.set_axis_off()

	# Update the size of the figure to fit the constructs drawn
	fig_x_dim = max_dna_len/70.0
	if fig_x_dim < 1.0:
		fig_x_dim = 1.0
	fig_y_dim = 1.2*len(ax_list)


	plt.gcf().set_size_inches( (fig_x_dim, fig_y_dim) )

	# Save the figure
	plt.tight_layout()
    #plt.subplots_adjust(hspace=0.001)


	fig.savefig(out_filename, transparent=True, dpi=300)

	if(True):
		pngfile = out_filename.replace('.pdf', '.png')
		fig.savefig(pngfile, transparent=True, dpi=300, format='png') 

	# Clear the plotting cache
	plt.close('all')


def is_valid_file(parser, arg):
    if not os.path.exists(arg):
        parser.error("The file %s does not exist!" % arg)
    else:
        return open(arg, 'r')  # return an open file handle


def main():	
	# Parse the arguments
	parser = ArgumentParser(description="file paths as arguments")
	parser.add_argument("-params", dest="params", required=True,
					help="plot_params.csv", metavar="FILE",
                    type=lambda x: is_valid_file(parser, x))
	parser.add_argument("-parts", dest="parts", required=True,
					help="parts_information.csv", metavar="FILE",
                    type=lambda x: is_valid_file(parser, x))
	parser.add_argument("-regulation", dest="regulation", required=False,
					help="reg_information.csv", metavar="FILE",
                    type=lambda x: is_valid_file(parser, x))
	parser.add_argument("-designs", dest="designs", required=True,
					help="dna_designs.csv", metavar="FILE",
                    type=lambda x: is_valid_file(parser, x))
	parser.add_argument("-output", dest="output_pdf", required=True,
					help="output pdf filename")
	args = parser.parse_args()

	# Process arguments
	plot_params = load_plot_parameters(args.params.name)
	part_info = load_part_information(args.parts.name)
	dna_designs = load_dna_designs (args.designs.name, part_info)

#	for param in plot_params.items():
#		print param
#	for part in part_info.items():
#		print part
#	for dna in dna_designs.items():
#		print dna[0]
#		for construct in dna[1]:
#			print construct
	
	regs_info = None
	if(args.regulation):
		regs_info = load_regulatory_information(args.regulation.name, part_info, dna_designs)
		
#		for reg in regs_info.items():
#			print reg

	plot_dna(dna_designs, args.output_pdf, plot_params, regs_info)

if __name__ == "__main__":
 	main()
