#!/usr/bin/env python
"""
	quick.py

	Quickly Plot SBOL Designs

	Usage:
	------
	python quick.py -input "p.gray p.lightblue i.lightred r.green c.orange t.purple -t.black -c.yellow -p.yellow" -output out.pdf
	allowed part types: 
	   p: promoter   i: ribozyme   r: rbs   c: cds   t: terminator   s: spacer   =: scar

	reverse part direction by using '-' before the 1-letter part type

	allowed colors
	   black, gray, red, orange, yellow, green, blue, purple, lightred, lightorange, 
	   lightyellow, lightgreen, lightblue, lightpurple
"""
#    Quickly Plot SBOL Designs
#    Copyright (C) 2014 by
#    Thomas E. Gorochowski <tom@chofski.co.uk>
#    Bryan Der <bder@mit.edu>
#    All rights reserved.
#    OSI Non-Profit Open Software License ("Non-Profit OSL") 3.0 license.

# Set the backend to use (important for headless servers)
import matplotlib
matplotlib.use('Agg')

# Other modules we require
import argparse
import dnaplotlib as dpl
import matplotlib.pyplot as plt

def process_arguments (input):
	# Types mapping
	types = {}
	types['p'] = 'Promoter'
	types['i'] = 'Ribozyme'
	types['r'] = 'RBS'
	types['c'] = 'CDS'
	types['t'] = 'Terminator'
	types['s'] = 'Spacer'
	types['='] = 'Scar'

	# Colours mapping
	colors = {}
	colors['black']  = (0.00,0.00,0.00)
	colors['gray']   = (0.60,0.60,0.60)
	colors['red']    = (0.89,0.10,0.11)
	colors['orange'] = (1.00,0.50,0.00)
	colors['yellow'] = (1.00,1.00,0.00)
	colors['green']  = (0.20,0.63,0.17)
	colors['blue']   = (0.12,0.47,0.71)
	colors['purple'] = (0.42,0.24,0.60)
	colors['lightred']    = (0.98,0.60,0.60)
	colors['lightorange'] = (0.99,0.75,0.44)
	colors['lightyellow'] = (1.00,1.00,0.60)
	colors['lightgreen']  = (0.70,0.87,0.54)
	colors['lightblue']   = (0.65,0.81,0.89)
	colors['lightpurple'] = (0.79,0.70,0.84)

	# Generate the parts list from the arguments
	part_list = []
	part_idx = 1
	for el in input.split(' '):
		if el != '':
			part_parts = el.split('.')
			if len(part_parts) == 2:
				part_short_type = part_parts[0]
				part_fwd = True
				if part_short_type[0] == '-':
					part_fwd = False
					part_short_type = el[1:]
				if part_short_type in types.keys():
					part_type = types[part_short_type]
					part_color = part_parts[1]
					part_rgb = (0,0,0)
					if part_color in colors.keys():
						part_rgb = colors[part_color]
					part_list.append( {'name'  : str(part_idx), 
						               'type'  : part_type, 
						               'fwd'   : part_fwd, 
						               'opts'  : {'color': part_rgb}} )
	return part_list


def main():
	# Parse the command line inputs
	parser = argparse.ArgumentParser(description="one line quick plot")
	parser.add_argument("-input",  dest="input",  required=True,  help="\"p.gray p.lightblue i.lightred r.green c.orange t.purple -t.black -c.yellow -p.yellow\"", metavar="string")
	parser.add_argument("-output", dest="output", required=False, help="output pdf filename")
	args = parser.parse_args()

	# Process the arguments
	design = process_arguments(args.input)

	# Create objects for plotting (dnaplotlib)
	dr = dpl.DNARenderer(linewidth=1.15, backbone_pad_left=3, backbone_pad_right=3)
	reg_renderers = dr.std_reg_renderers()
	part_renderers = dr.SBOL_part_renderers()
	regs = None

	# Generate the figure
	fig = plt.figure(figsize=(5.0,5.0))
	ax = fig.add_subplot(1,1,1)
	
	# Plot the design
	dna_start, dna_end = dr.renderDNA(ax, design, part_renderers, regs, reg_renderers)
	max_dna_len = dna_end-dna_start
	
	# Format the axis
	ax.set_xticks([])
	ax.set_yticks([])
	
	# Set bounds
	ax.set_xlim([(-0.01*max_dna_len),
		        max_dna_len+(0.01*max_dna_len)])
	ax.set_ylim([-35,35])
	ax.set_aspect('equal')
	ax.set_axis_off()
	
	# Update the size of the figure to fit the constructs drawn
	fig_x_dim = max_dna_len/60.0
	if fig_x_dim < 1.0:
		fig_x_dim = 1.0
	fig_y_dim = 1.2
	plt.gcf().set_size_inches( (fig_x_dim, fig_y_dim) )
	
	# Save the figure
	plt.tight_layout()
	fig.savefig(args.output, transparent=True)
	

# Enable the script to be run from the command line	
if __name__ == "__main__":
	main()
