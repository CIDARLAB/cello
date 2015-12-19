#!/usr/bin/env python
"""
dnaplotlib
==========
    This module is designed to allow for highly customisable visualisation of DNA
    fragments. Diagrams can be in the form of conceptual SBOL compliant icons or
    make use of icons whose width is scaled to allow for easier comparison of part 
    locations to trace information, such as for corresponding RNA-seq read depth
    data. All plotting is performed using matplotlib and to an axis object. This 
    enables the export of publication quality, vector-based figures. Furthermore,
    all standard renderers can be replaced with user defined versions to allow 
    for full customisation of the plot.

    To make use of this module it is necessary to create the rendering object 
    after importing the module:

    >  import dnaplotlib as dpl
    >  dr = dpl.DNARenderer()

    This object performs all rendering using the renderDNA() method. To describe
    what should be plotted, dnaplotlib requires the DNA design in a specific 
    format. For standard SBOL diagrams a design is a list of dictionaries where
    each dictionary relates to a specific part and as a minimum contains the
    keys:

    - name: A name that can be potentially used in regulation.
    - type: The type of part (decides which renderer to use).
    - fwd: Boolean defing if the part is in a forward orientation.

    Once this list is defined and an axis object is created the design can be
    draw using standard renders and to a user created matplotlib axes by running:

    > reg_renderers = dr.std_reg_renderers()
	> part_renderers = dr.SBOL_part_renderers()
	> regs = None
	> design = ... Design is created here ...
	> ax = ... matplotlib axes created here ...
    > start, end = dr.renderDNA(ax, design, part_renderers, regs, reg_renderers)

    The function returns the start and end point of the design which can then
    be used for resizing the axes and figure. For more advanced use cases we 
    advise looking at the gallery distributed with this module.
"""
#    dnaplotlib
#    Copyright (C) 2014 by
#    Thomas E. Gorochowski <tom@chofski.co.uk>
#    Emerson Glassey <eglassey@mit.edu>
#    Bryan Der <bder@mit.edu>
#    All rights reserved.
#    OSI Non-Profit Open Software License ("Non-Profit OSL") 3.0 license.

# Set the backend to use (important for headless servers)
import matplotlib
matplotlib.use('Agg')

from matplotlib.patches import Polygon, Ellipse, Wedge, Circle, PathPatch
from matplotlib.path import Path
from matplotlib.lines import Line2D
from matplotlib.patheffects import Stroke 
import math

__author__  = 'Thomas E. Gorochowski <tom@chofski.co.uk>, Voigt Lab, MIT\n\
               Emerson Glassey <eglassey@mit.edu>, Voigt Lab, MIT\n\
               Bryan Der <bder@mit.edu>, Voigt Lab, MIT'
__license__ = 'OSI Non-Profit OSL 3.0'
__version__ = '1.0'

###############################################################################
# SBOL Compliant Icon Renderers
###############################################################################

def write_label (ax, label_text, x_pos, opts=None):
	""" Renders labels on parts.
	"""
	label_style = 'normal'
	label_size = 7
	label_y_offset = 0
	label_x_offset = 0
	if opts != None:
		if 'label_style' in opts.keys():
			label_style = opts['label_style']
		if 'label_size' in opts.keys():
			label_size = opts['label_size']
		if 'label_y_offset' in opts.keys():
			label_y_offset = opts['label_y_offset']
		if 'label_x_offset' in opts.keys():
			label_x_offset = opts['label_x_offset']
	ax.text(x_pos+label_x_offset, label_y_offset, label_text, horizontalalignment='center',
		    verticalalignment='center', fontsize=label_size, fontstyle=label_style, zorder=30)

def sbol_promoter (ax, type, num, start, end, prev_end, scale, linewidth, opts):
	""" Built-in SBOL promoter renderer.
	"""
	# Default options
	color = (0.0,0.0,0.0)
	start_pad = 2.0
	end_pad = 2.0
	y_extent = 10
	x_extent = 10
	arrowhead_height = 2
	arrowhead_length = 4
	# Reset defaults if provided
	if opts != None:
		if 'color' in opts.keys():
			color = opts['color']
		if 'start_pad' in opts.keys():
			start_pad = opts['start_pad']
		if 'end_pad' in opts.keys():
			end_pad = opts['end_pad']
		if 'y_extent' in opts.keys():
			y_extent = opts['y_extent']
		if 'x_extent' in opts.keys():
			x_extent = opts['x_extent']
		if 'arrowhead_height' in opts.keys():
			arrowhead_height = opts['arrowhead_height']
		if 'arrowhead_length' in opts.keys():
			arrowhead_length = opts['arrowhead_length']
		if 'linewidth' in opts.keys():
			linewidth = opts['linewidth']
		if 'scale' in opts.keys():
			scale = opts['scale']
	# Check direction add start padding
	dir_fac = 1.0
	final_end = end
	final_start = prev_end
	if start > end:
		dir_fac = -1.0
		start = prev_end+end_pad+x_extent
		end = prev_end+end_pad
		final_end = start+start_pad
	else:
		start = prev_end+start_pad
		end = start+x_extent
		final_end = end+end_pad
	# Draw the promoter symbol
	l1 = Line2D([start,start],[0,dir_fac*y_extent], linewidth=linewidth, 
		        color=color, zorder=9)
	l2 = Line2D([start,start+dir_fac*x_extent-dir_fac*(arrowhead_length*0.5)],
                [dir_fac*y_extent,dir_fac*x_extent], linewidth=linewidth, 
                color=color, zorder=10)
	ax.add_line(l1)
	ax.add_line(l2)
	p1 = Polygon([(start+dir_fac*x_extent-dir_fac*arrowhead_length, 
		           dir_fac*y_extent+(arrowhead_height)), 
		          (start+dir_fac*x_extent, dir_fac*y_extent),
		          (start+dir_fac*x_extent-dir_fac*arrowhead_length, 
		           dir_fac*y_extent-(arrowhead_height))],
		          facecolor=color, edgecolor=color, linewidth=linewidth, 
		          path_effects=[Stroke(joinstyle="miter")]) # This is a work around for matplotlib < 1.4.0
	ax.add_patch(p1)
	if opts != None and 'label' in opts.keys():
		if final_start > final_end:
			write_label(ax, opts['label'], final_end+((final_start-final_end)/2.0), opts=opts)
		else:
			write_label(ax, opts['label'], final_start+((final_end-final_start)/2.0), opts=opts)
	if final_start > final_end:
		return prev_end, final_start
	else:
		return prev_end, final_end

def sbol_cds (ax, type, num, start, end, prev_end, scale, linewidth, opts):
	""" Built-in SBOL coding sequence renderer.
	"""
	# Default options
	color = (0.7,0.7,0.7)
	hatch = ''
	start_pad = 1.0
	end_pad = 1.0
	y_extent = 5
	x_extent = 30
	arrowhead_height = 4
	arrowhead_length = 8
	# Reset defaults if provided
	if opts != None:
		if 'color' in opts.keys():
			color = opts['color']
		if 'hatch' in opts.keys():
			hatch = opts['hatch']
		if 'start_pad' in opts.keys():
			start_pad = opts['start_pad']
		if 'end_pad' in opts.keys():
			end_pad = opts['end_pad']
		if 'y_extent' in opts.keys():
			y_extent = opts['y_extent']
		if 'x_extent' in opts.keys():
			x_extent = opts['x_extent']
		if 'arrowhead_height' in opts.keys():
			arrowhead_height = opts['arrowhead_height']
		if 'arrowhead_length' in opts.keys():
			arrowhead_length = opts['arrowhead_length']
		if 'linewidth' in opts.keys():
			linewidth = opts['linewidth']
		if 'scale' in opts.keys():
			scale = opts['scale']
	# Check direction add start padding
	dir_fac = 1.0
	final_end = end
	final_start = prev_end
	if start > end:
		dir_fac = -1.0
		start = prev_end+end_pad+x_extent
		end = prev_end+end_pad
		final_end = start+start_pad
	else:
		start = prev_end+start_pad
		end = start+x_extent
		final_end = end+end_pad
	# Draw the CDS symbol
	p1 = Polygon([(start, y_extent), 
		          (start, -y_extent),
		          (end-dir_fac*arrowhead_length, -y_extent),
		          (end-dir_fac*arrowhead_length, -y_extent-arrowhead_height),
		          (end, 0),
		          (end-dir_fac*arrowhead_length, y_extent+arrowhead_height),
		          (end-dir_fac*arrowhead_length, y_extent)],
		          edgecolor=(0.0,0.0,0.0), facecolor=color, linewidth=linewidth, 
		          hatch=hatch, zorder=11, 
		          path_effects=[Stroke(joinstyle="miter")]) # This is a work around for matplotlib < 1.4.0
	ax.add_patch(p1)
	if opts != None and 'label' in opts.keys():
		if final_start > final_end:
			write_label(ax, opts['label'], final_end+((final_start-final_end)/2.0), opts=opts)
		else:
			write_label(ax, opts['label'], final_start+((final_end-final_start)/2.0), opts=opts)
	if final_start > final_end:
		return prev_end, final_start
	else:
		return prev_end, final_end

def sbol_terminator (ax, type, num, start, end, prev_end, scale, linewidth, opts):
	""" Built-in SBOL terminator renderer.
	"""
	# Default options
	color = (0,0,0)
	start_pad = 2.0
	end_pad = 2.0
	y_extent = 10.0
	x_extent = 8.0
	# Reset defaults if provided
	if opts != None:
		if 'color' in opts.keys():
			color = opts['color']
		if 'start_pad' in opts.keys():
			start_pad = opts['start_pad']
		if 'end_pad' in opts.keys():
			end_pad = opts['end_pad']
		if 'y_extent' in opts.keys():
			y_extent = opts['y_extent']
		if 'x_extent' in opts.keys():
			x_extent = opts['x_extent']
		if 'linewidth' in opts.keys():
			linewidth = opts['linewidth']
		if 'scale' in opts.keys():
			scale = opts['scale']
	# Check direction add start padding
	dir_fac = 1.0
	final_end = end
	final_start = prev_end
	if start > end:
		dir_fac = -1.0
		start = prev_end+end_pad+x_extent
		end = prev_end+end_pad
		final_end = start+start_pad
	else:
		start = prev_end+start_pad
		end = start+x_extent
		final_end = end+end_pad
	# Draw the terminator symbol
	l1 = Line2D([start+dir_fac*(x_extent/2.0),start+dir_fac*(x_extent/2.0)],[0,dir_fac*y_extent], linewidth=linewidth, 
		        color=color, zorder=8)
	l2 = Line2D([start,start+(dir_fac*x_extent)],[dir_fac*y_extent,dir_fac*y_extent], 
		        linewidth=linewidth, color=color, zorder=9)
	ax.add_line(l1)
	ax.add_line(l2)
	if opts != None and 'label' in opts.keys():
		if final_start > final_end:
			write_label(ax, opts['label'], final_end+((final_start-final_end)/2.0), opts=opts)
		else:
			write_label(ax, opts['label'], final_start+((final_end-final_start)/2.0), opts=opts)
	if final_start > final_end:
		return prev_end, final_start
	else:
		return prev_end, final_end

def sbol_rbs (ax, type, num, start, end, prev_end, scale, linewidth, opts):
	""" Built-in SBOL ribosome binding site renderer.
	"""
	# Default options
	color = (0.7,0.7,0.7)
	start_pad = 2.0
	end_pad = 2.0
	x_extent = 10.0
	# Reset defaults if provided
	if opts != None:
		if 'color' in opts.keys():
			color = opts['color']
		if 'start_pad' in opts.keys():
			start_pad = opts['start_pad']
		if 'end_pad' in opts.keys():
			end_pad = opts['end_pad']
		if 'x_extent' in opts.keys():
			x_extent = opts['x_extent']
		if 'linewidth' in opts.keys():
			linewidth = opts['linewidth']
		if 'scale' in opts.keys():
			scale = opts['scale']
	# Check direction add start padding
	dir_fac = 1.0
	final_end = end
	final_start = prev_end
	rbs_center = (0,0)
	if start > end:
		start = prev_end+end_pad+x_extent
		end = prev_end+end_pad
		final_end = start+start_pad
		rbs_center = (end+((start-end)/2.0),0)
		w1 = Wedge(rbs_center, x_extent/2.0, 180, 360, linewidth=linewidth, 
			       facecolor=color, zorder=8)
		ax.add_patch(w1)
	else:
		start = prev_end+start_pad
		end = start+x_extent
		final_end = end+end_pad
		rbs_center = (start+((end-start)/2.0),0)
		w1 = Wedge(rbs_center, x_extent/2.0, 0, 180, linewidth=linewidth, 
			       facecolor=color, zorder=8)
		ax.add_patch(w1)
	if opts != None and 'label' in opts.keys():
		if final_start > final_end:
			write_label(ax, opts['label'], final_end+((final_start-final_end)/2.0), opts=opts)
		else:
			write_label(ax, opts['label'], final_start+((final_end-final_start)/2.0), opts=opts)
	if final_start > final_end:
		return prev_end, final_start
	else:
		return prev_end, final_end


def sbol_ribozyme (ax, type, num, start, end, prev_end, scale, linewidth, opts):
	""" Built-in SBOL ribozyme renderer.
	"""
	return stick_figure(ax,type,num,start,end,prev_end,scale,linewidth,opts)	
def sbol_protein_stability (ax, type, num, start, end, prev_end, scale, linewidth, opts):
	""" Built-in SBOL protein stability element renderer.
	"""
	return stick_figure(ax,type,num,start,end,prev_end,scale,linewidth,opts)	
def sbol_protease (ax, type, num, start, end, prev_end, scale, linewidth, opts):
	""" Built-in SBOL protease site renderer.
	"""
	return stick_figure(ax,type,num,start,end,prev_end,scale,linewidth,opts)
def sbol_ribonuclease (ax, type, num, start, end, prev_end, scale, linewidth, opts):
	""" Built-in SBOL ribonuclease site renderer.
	"""
	return stick_figure(ax,type,num,start,end,prev_end,scale,linewidth,opts)

def stick_figure (ax, type, num, start, end, prev_end, scale, linewidth, opts):
	""" General function for drawing stick based parts (e.g., ribozyme and protease sites).
	"""
	# Default options
	color = (0,0,0)
	start_pad = 2.0
	end_pad = 2.0
	x_extent = 5.0
	y_extent = 10.0
	linestyle = '-'
	
	linetype  = "";
	shapetype = "";
	if(type == "Ribozyme"):
		linetype = 'dash'
		headgroup = 'O'
	elif(type == "Protease"):
		linetype = 'dash'
		headgroup = 'X'
	elif(type == "ProteinStability"):
		linetype = 'solid'
		headgroup = 'O'
	elif(type == "Ribonuclease"):
		linetype = 'solid'
		headgroup = 'X'

	# Reset defaults if provided
	if opts != None:
		if 'color' in opts.keys():
			color = opts['color']
		if 'start_pad' in opts.keys():
			start_pad = opts['start_pad']
		if 'end_pad' in opts.keys():
			end_pad = opts['end_pad']
		if 'x_extent' in opts.keys():
			x_extent = opts['x_extent']
		if 'y_extent' in opts.keys():
			y_extent = opts['y_extent']
		if 'linestyle' in opts.keys():
			linestyle = opts['linestyle']
		if 'linewidth' in opts.keys():
			linewidth = opts['linewidth']
		if 'scale' in opts.keys():
			scale = opts['scale']
	# Check direction add start padding
	final_end = end
	final_start = prev_end

	if start > end:
		start = prev_end+end_pad+x_extent
		end = prev_end+end_pad
		final_end = start+start_pad
		rbs_center = (end+((start-end)/2.0),-y_extent)
		c1 = Circle(rbs_center, x_extent/2.0, linewidth=linewidth, edgecolor=color, 
			        facecolor=(1,1,1), zorder=8)
		x1 = Line2D([start,end],[-y_extent*1.25,-y_extent/1.5], 
		        	linewidth=linewidth, color=color, zorder=12, linestyle='-')
		x2 = Line2D([start,end],[-y_extent/1.5,-y_extent*1.25], 
		        	linewidth=linewidth, color=color, zorder=12, linestyle='-')

		dash1  = Line2D([end+((start-end)/2.0),end+((start-end)/2.0)],[0,-y_extent/4], 
			        linewidth=linewidth, color=color, zorder=8, linestyle=linestyle)
		dash2  = Line2D([end+((start-end)/2.0),end+((start-end)/2.0)],[-y_extent/2,-y_extent+(x_extent/2.0)], 
			        linewidth=linewidth, color=color, zorder=8, linestyle=linestyle)
		solidO = Line2D([end+((start-end)/2.0),end+((start-end)/2.0)],[0,-y_extent+(x_extent/2.0)], 
			        linewidth=linewidth, color=color, zorder=8, linestyle=linestyle)
		solidX = Line2D([end+((start-end)/2.0),end+((start-end)/2.0)],[0,-y_extent], 
			        linewidth=linewidth, color=color, zorder=8, linestyle=linestyle)

		if(headgroup == "O" and linetype == "dash"):
			ax.add_patch(c1)
			ax.add_line(dash1)
			ax.add_line(dash2)
		elif(headgroup == "X" and linetype == "dash"):
			ax.add_line(x1)
			ax.add_line(x2)
			ax.add_line(dash1)
			ax.add_line(dash2)
		elif(headgroup == "O" and linetype == "solid"):
			ax.add_patch(c1)
			ax.add_line(solidO)
		elif(headgroup == "X" and linetype == "solid"):
			ax.add_line(x1)
			ax.add_line(x2)
			ax.add_line(solidX)
		
	else:
		start = prev_end+start_pad
		end = start+x_extent
		final_end = end+end_pad
		rbs_center = (start+((end-start)/2.0),y_extent)
		c1 = Circle(rbs_center, x_extent/2.0, linewidth=linewidth, edgecolor=color, 
			        facecolor=(1,1,1), zorder=8)
		x1 = Line2D([start,end],[y_extent*1.25,y_extent/1.5], 
		        	linewidth=linewidth, color=color, zorder=12, linestyle='-')
		x2 = Line2D([start,end],[y_extent/1.5,y_extent*1.25], 
		        	linewidth=linewidth, color=color, zorder=12, linestyle='-')

		dash1 = Line2D([end+((start-end)/2.0),end+((start-end)/2.0)],[0,y_extent/4], 
			        linewidth=linewidth, color=color, zorder=8, linestyle=linestyle)
		dash2 = Line2D([end+((start-end)/2.0),end+((start-end)/2.0)],[y_extent/2,y_extent-(x_extent/2.0)], 
			        linewidth=linewidth, color=color, zorder=8, linestyle=linestyle)
		solidO = Line2D([end+((start-end)/2.0),end+((start-end)/2.0)],[0,y_extent-(x_extent/2.0)], 
			        linewidth=linewidth, color=color, zorder=8, linestyle=linestyle)
		solidX = Line2D([end+((start-end)/2.0),end+((start-end)/2.0)],[0,y_extent], 
			        linewidth=linewidth, color=color, zorder=8, linestyle=linestyle)

		if(headgroup == 'O' and linetype == 'dash'):
			ax.add_patch(c1)
			ax.add_line(dash1)
			ax.add_line(dash2)
		elif(headgroup == "X" and linetype == "dash"):
			ax.add_line(x1)
			ax.add_line(x2)
			ax.add_line(dash1)
			ax.add_line(dash2)
		elif(headgroup == "O" and linetype == "solid"):
			ax.add_patch(c1)
			ax.add_line(solidO)
		elif(headgroup == "X" and linetype == "solid"):
			ax.add_line(x1)
			ax.add_line(x2)
			ax.add_line(solidX)
	
	if opts != None and 'label' in opts.keys():
		if final_start > final_end:
			write_label(ax, opts['label'], final_end+((final_start-final_end)/2.0), opts=opts)
		else:
			write_label(ax, opts['label'], final_start+((final_end-final_start)/2.0), opts=opts)

	if final_start > final_end:
		return prev_end, final_start
	else:
		return prev_end, final_end


def sbol_scar (ax, type, num, start, end, prev_end, scale, linewidth, opts):
	""" Built-in SBOL scar renderer.
	"""
	# Default options
	color = (0,0,0)
	start_pad = 2.0
	end_pad = 2.0
	x_extent = 6.0
	y_extent = 1.0
	linestyle = '-'
	# Reset defaults if provided
	if opts != None:
		if 'color' in opts.keys():
			color = opts['color']
		if 'start_pad' in opts.keys():
			start_pad = opts['start_pad']
		if 'end_pad' in opts.keys():
			end_pad = opts['end_pad']
		if 'x_extent' in opts.keys():
			x_extent = opts['x_extent']
		if 'y_extent' in opts.keys():
			y_extent = opts['y_extent']
		if 'linestyle' in opts.keys():
			linestyle = opts['linestyle']
		if 'linewidth' in opts.keys():
			linewidth = opts['linewidth']
		if 'scale' in opts.keys():
			scale = opts['scale']
	# Check direction add start padding
	final_end = end
	final_start = prev_end

	start = prev_end+start_pad
	end = start+x_extent
	final_end = end+end_pad
	
	l_top    = Line2D([start,start+x_extent],[y_extent,y_extent], 
		        linewidth=linewidth, color=color, zorder=12, linestyle=linestyle)
	l_bottom = Line2D([start,start+x_extent],[-1*y_extent,-1*y_extent], 
		        linewidth=linewidth, color=color, zorder=12, linestyle=linestyle)
	#white rectangle overlays backbone line
	p1 = Polygon([(start, y_extent), 
		          (start, -y_extent),
		          (start+x_extent, -y_extent),
		          (start+x_extent, y_extent)],
		          edgecolor=(1,1,1), facecolor=(1,1,1), linewidth=linewidth, zorder=11, 
		          path_effects=[Stroke(joinstyle="miter")]) # This is a work around for matplotlib < 1.4.0)

	ax.add_patch(p1)
	ax.add_line(l_top)
	ax.add_line(l_bottom)

	if opts != None and 'label' in opts.keys():
		if final_start > final_end:
			write_label(ax, opts['label'], final_end+((final_start-final_end)/2.0), opts=opts)
		else:
			write_label(ax, opts['label'], final_start+((final_end-final_start)/2.0), opts=opts)

	if final_start > final_end:
		return prev_end, final_start
	else:
		return prev_end, final_end

def sbol_empty_space (ax, type, num, start, end, prev_end, scale, linewidth, opts):
	""" Built-in empty space renderer.
	"""
	# Default options
	x_extent = 12.0
	# Reset defaults if provided
	if opts != None:
		if 'x_extent' in opts.keys():
			x_extent = opts['x_extent']
	# Check direction add start padding
	final_start = prev_end
	final_end = final_start+x_extent

	if opts != None and 'label' in opts.keys():
		if final_start > final_end:
			write_label(ax, opts['label'], final_end+((final_start-final_end)/2.0), opts=opts)
		else:
			write_label(ax, opts['label'], final_start+((final_end-final_start)/2.0), opts=opts)
	if final_start > final_end:
		return prev_end, final_start
	else:
		return prev_end, final_end


def sbol_5_overhang (ax, type, num, start, end, prev_end, scale, linewidth, opts):
	""" Built-in SBOL 5' overhang renderer.
	"""
	# Default options
	color = (0,0,0)
	start_pad = 0.0
	end_pad = 2.0
	x_extent = 6.0
	y_extent = 1.0
	linestyle = '-'
	# Reset defaults if provided
	if opts != None:
		if 'color' in opts.keys():
			color = opts['color']
		if 'start_pad' in opts.keys():
			start_pad = opts['start_pad']
		if 'end_pad' in opts.keys():
			end_pad = opts['end_pad']
		if 'x_extent' in opts.keys():
			x_extent = opts['x_extent']
		if 'y_extent' in opts.keys():
			y_extent = opts['y_extent']
		if 'linestyle' in opts.keys():
			linestyle = opts['linestyle']
		if 'linewidth' in opts.keys():
			linewidth = opts['linewidth']
		if 'scale' in opts.keys():
			scale = opts['scale']
	# Check direction add start padding
	final_end = end
	final_start = prev_end

	start = prev_end+start_pad
	end = start+x_extent
	final_end = end+end_pad
	
	l_top    = Line2D([start,start+x_extent],[y_extent,y_extent], 
		        linewidth=linewidth, color=color, zorder=12, linestyle=linestyle)
	l_bottom = Line2D([start+(x_extent/2.0),start+x_extent],[-1*y_extent,-1*y_extent], 
		        linewidth=linewidth, color=color, zorder=12, linestyle=linestyle)
	#white rectangle overlays backbone line
	p1 = Polygon([(start, y_extent), 
		          (start, -y_extent),
		          (start+x_extent, -y_extent),
		          (start+x_extent, y_extent)],
		          edgecolor=(1,1,1), facecolor=(1,1,1), linewidth=linewidth, zorder=11, 
		          path_effects=[Stroke(joinstyle="miter")]) # This is a work around for matplotlib < 1.4.0)		

	ax.add_patch(p1)
	ax.add_line(l_top)
	ax.add_line(l_bottom)

	if opts != None and 'label' in opts.keys():
		if final_start > final_end:
			write_label(ax, opts['label'], final_end+((final_start-final_end)/2.0), opts=opts)
		else:
			write_label(ax, opts['label'], final_start+((final_end-final_start)/2.0), opts=opts)

	if final_start > final_end:
		return prev_end, final_start
	else:
		return prev_end, final_end

def sbol_3_overhang (ax, type, num, start, end, prev_end, scale, linewidth, opts):
	""" Built-in SBOL 3' overhang renderer.
	"""
	# Default options
	color = (0,0,0)
	start_pad = 2.0
	end_pad = 0.0
	x_extent = 6.0
	y_extent = 1.0
	linestyle = '-'
	# Reset defaults if provided
	if opts != None:
		if 'color' in opts.keys():
			color = opts['color']
		if 'start_pad' in opts.keys():
			start_pad = opts['start_pad']
		if 'end_pad' in opts.keys():
			end_pad = opts['end_pad']
		if 'x_extent' in opts.keys():
			x_extent = opts['x_extent']
		if 'y_extent' in opts.keys():
			y_extent = opts['y_extent']
		if 'linestyle' in opts.keys():
			linestyle = opts['linestyle']
		if 'linewidth' in opts.keys():
			linewidth = opts['linewidth']
		if 'scale' in opts.keys():
			scale = opts['scale']
	# Check direction add start padding
	final_end = end
	final_start = prev_end

	start = prev_end+start_pad
	end = start+x_extent
	final_end = end+end_pad
	
	l_top    = Line2D([start,start+x_extent],[y_extent,y_extent], 
		        linewidth=linewidth, color=color, zorder=12, linestyle=linestyle)
	l_bottom = Line2D([start,start+(x_extent/2.0)],[-1*y_extent,-1*y_extent], 
		        linewidth=linewidth, color=color, zorder=12, linestyle=linestyle)
	#white rectangle overlays backbone line
	p1 = Polygon([(start, y_extent), 
		          (start, -y_extent),
		          (start+x_extent, -y_extent),
		          (start+x_extent, y_extent)],
		          edgecolor=(1,1,1), facecolor=(1,1,1), linewidth=linewidth, zorder=11, 
		          path_effects=[Stroke(joinstyle="miter")]) # This is a work around for matplotlib < 1.4.0)		

	ax.add_patch(p1)
	ax.add_line(l_top)
	ax.add_line(l_bottom)

	if opts != None and 'label' in opts.keys():
		if final_start > final_end:
			write_label(ax, opts['label'], final_end+((final_start-final_end)/2.0), opts=opts)
		else:
			write_label(ax, opts['label'], final_start+((final_end-final_start)/2.0), opts=opts)

	if final_start > final_end:
		return prev_end, final_start
	else:
		return prev_end, final_end

def sbol_blunt_restriction_site (ax, type, num, start, end, prev_end, scale, linewidth, opts):
	""" Built-in SBOL blunt-end restriction site renderer.
	"""
	# Default options
	color = (0,0,0)
	start_pad = 2.0
	end_pad = 2.0
	y_extent = 4.0
	x_extent = 1.5
	site_space = 1.5
	linestyle = '-'
	# Reset defaults if provided
	if opts != None:
		if 'color' in opts.keys():
			color = opts['color']
		if 'site_space' in opts.keys():
			site_space = opts['site_space']
		if 'start_pad' in opts.keys():
			start_pad = opts['start_pad']
		if 'end_pad' in opts.keys():
			end_pad = opts['end_pad']
		if 'x_extent' in opts.keys():
			x_extent = opts['x_extent']
		if 'y_extent' in opts.keys():
			y_extent = opts['y_extent']
		if 'linestyle' in opts.keys():
			linestyle = opts['linestyle']
		if 'linewidth' in opts.keys():
			linewidth = opts['linewidth']
		if 'scale' in opts.keys():
			scale = opts['scale']
	
	# Direction is meaningless for this part => start is always < end
	if start > end:
		temp_end = end
		end = start
		start = temp_end

	# Check direction add start padding
	final_end = end
	final_start = prev_end
	start = prev_end+start_pad
	end = start+x_extent+site_space+x_extent
	final_end = end+end_pad
	
	l1        = Line2D([start+x_extent,start+x_extent],[-y_extent,y_extent], 
		           linewidth=linewidth, color=color, zorder=12, linestyle=linestyle)
	l1_top    = Line2D([start,start+x_extent],[y_extent,y_extent], 
		           linewidth=linewidth, color=color, zorder=12, linestyle=linestyle)
	l1_bottom = Line2D([start,start+x_extent],[-y_extent,-y_extent], 
		           linewidth=linewidth, color=color, zorder=12, linestyle=linestyle)

	l2        = Line2D([end-x_extent,end-x_extent],[-y_extent,y_extent], 
		           linewidth=linewidth, color=color, zorder=12, linestyle=linestyle)
	l2_top    = Line2D([end,end-x_extent],[y_extent,y_extent], 
		           linewidth=linewidth, color=color, zorder=12, linestyle=linestyle)
	l2_bottom = Line2D([end,end-x_extent],[-y_extent,-y_extent], 
		           linewidth=linewidth, color=color, zorder=12, linestyle=linestyle)
	
	ax.add_line(l1)
	ax.add_line(l1_top)
	ax.add_line(l1_bottom)
	ax.add_line(l2)
	ax.add_line(l2_top)
	ax.add_line(l2_bottom)

	if opts != None and 'label' in opts.keys():
		write_label(ax, opts['label'], final_start+((final_end-final_start)/2.0), opts=opts)

	return final_start, final_end

def sbol_primer_binding_site (ax, type, num, start, end, prev_end, scale, linewidth, opts):
	""" Built-in SBOL primer binding site renderer.
	"""
	# Default options
	color = (0,0,0)
	start_pad = 2.0
	end_pad = 2.0
	y_extent = 2.0
	y_offset = 1.5
	x_extent = 8.0
	arrowhead_length = 2.0
	linestyle = '-'
	# Reset defaults if provided
	if opts != None:
		if 'color' in opts.keys():
			color = opts['color']
		if 'start_pad' in opts.keys():
			start_pad = opts['start_pad']
		if 'end_pad' in opts.keys():
			end_pad = opts['end_pad']
		if 'x_extent' in opts.keys():
			x_extent = opts['x_extent']
		if 'y_extent' in opts.keys():
			y_extent = opts['y_extent']
		if 'y_offset' in opts.keys():
			y_offset = opts['y_offset']
		if 'arrowhead_length' in opts.keys():
			arrowhead_length = opts['arrowhead_length']
		if 'linestyle' in opts.keys():
			linestyle = opts['linestyle']
		if 'linewidth' in opts.keys():
			linewidth = opts['linewidth']
		if 'scale' in opts.keys():
			scale = opts['scale']
	
	direction = 'F'
	if start > end:
		direction = 'R'
		temp_end = end
		end = start
		start = temp_end

	final_end = prev_end
	final_start = prev_end

	if direction == 'F':
		final_start = prev_end
		start = prev_end+start_pad
		end = start+x_extent
		final_end = end+end_pad
	else:
		final_start = prev_end
		end = prev_end+end_pad
		start = end+x_extent
		final_start = start+start_pad

	if direction == 'F':
		verts = [(start, y_offset), (end, y_offset), (end-arrowhead_length, y_offset+y_extent)]
		codes = [Path.MOVETO, Path.LINETO, Path.LINETO]
		path = Path(verts, codes)
		patch = PathPatch(path, lw=linewidth, edgecolor=color, facecolor=(1,1,1))
		ax.add_patch(patch)
	else:
		verts = [(start, -y_offset), (end, -y_offset), (end+arrowhead_length, -y_offset-y_extent)]
		codes = [Path.MOVETO, Path.LINETO, Path.LINETO]
		path = Path(verts, codes)
		patch = PathPatch(path, lw=linewidth, edgecolor=color, facecolor=(1,1,1))
		ax.add_patch(patch)

	if opts != None and 'label' in opts.keys():
		if start > end:
			write_label(ax, opts['label'], end+((start-end)/2.0), opts=opts)
		else:
			write_label(ax, opts['label'], start+((end-start)/2.0), opts=opts)

	if final_start > final_end:
		return prev_end, final_start
	else:
		return prev_end, final_end

def sbol_5_sticky_restriction_site  (ax, type, num, start, end, prev_end, scale, linewidth, opts):
	""" Built-in SBOL 5' sticky-end restriction site renderer.
	"""
	# Default options
	color = (0,0,0)
	start_pad = 2.0
	end_pad = 2.0
	y_extent = 4.0
	x_extent = 8.0
	end_space = 1.0
	linestyle = '-'
	# Reset defaults if provided
	if opts != None:
		if 'color' in opts.keys():
			color = opts['color']
		if 'end_space' in opts.keys():
			end_space = opts['end_space']
		if 'start_pad' in opts.keys():
			start_pad = opts['start_pad']
		if 'end_pad' in opts.keys():
			end_pad = opts['end_pad']
		if 'x_extent' in opts.keys():
			x_extent = opts['x_extent']
		if 'y_extent' in opts.keys():
			y_extent = opts['y_extent']
		if 'linestyle' in opts.keys():
			linestyle = opts['linestyle']
		if 'linewidth' in opts.keys():
			linewidth = opts['linewidth']
		if 'scale' in opts.keys():
			scale = opts['scale']
	
	# Direction is meaningless for this part => start is always < end
	if start > end:
		temp_end = end
		end = start
		start = temp_end

	# Check direction add start padding
	final_end = end
	final_start = prev_end
	start = prev_end+start_pad
	end = start+end_space+x_extent+end_space
	final_end = end+end_pad
	
	l1        = Line2D([start+end_space,start+end_space+x_extent],[0,0], 
		           linewidth=linewidth, color=color, zorder=12, linestyle=linestyle)
	l1_top    = Line2D([start+end_space,start+end_space],[0,y_extent], 
		           linewidth=linewidth, color=color, zorder=12, linestyle=linestyle)
	l1_bottom = Line2D([start+end_space+x_extent,start+end_space+x_extent],[0,-y_extent], 
		           linewidth=linewidth, color=color, zorder=12, linestyle=linestyle)
	ax.add_line(l1)
	ax.add_line(l1_top)
	ax.add_line(l1_bottom)

	# White rectangle overlays backbone line
	p1 = Polygon([(start, y_extent), 
		          (start, -y_extent),
		          (end, -y_extent),
		          (end, y_extent)],
		          edgecolor=(1,1,1), facecolor=(1,1,1), linewidth=linewidth, zorder=11, 
		          path_effects=[Stroke(joinstyle="miter")]) # This is a work around for matplotlib < 1.4.0)		

	ax.add_patch(p1)

	if opts != None and 'label' in opts.keys():
		write_label(ax, opts['label'], final_start+((final_end-final_start)/2.0), opts=opts)

	return final_start, final_end

def sbol_3_sticky_restriction_site  (ax, type, num, start, end, prev_end, scale, linewidth, opts):
	""" Built-in SBOL 3' sticky-end restriction site renderer.
	"""
	# Default options
	color = (0,0,0)
	start_pad = 2.0
	end_pad = 2.0
	y_extent = 4.0
	x_extent = 8.0
	end_space = 1.0
	linestyle = '-'
	# Reset defaults if provided
	if opts != None:
		if 'color' in opts.keys():
			color = opts['color']
		if 'end_space' in opts.keys():
			end_space = opts['end_space']
		if 'start_pad' in opts.keys():
			start_pad = opts['start_pad']
		if 'end_pad' in opts.keys():
			end_pad = opts['end_pad']
		if 'x_extent' in opts.keys():
			x_extent = opts['x_extent']
		if 'y_extent' in opts.keys():
			y_extent = opts['y_extent']
		if 'linestyle' in opts.keys():
			linestyle = opts['linestyle']
		if 'linewidth' in opts.keys():
			linewidth = opts['linewidth']
		if 'scale' in opts.keys():
			scale = opts['scale']
	
	# Direction is meaningless for this part => start is always < end
	if start > end:
		temp_end = end
		end = start
		start = temp_end

	# Check direction add start padding
	final_end = end
	final_start = prev_end
	start = prev_end+start_pad
	end = start+end_space+x_extent+end_space
	final_end = end+end_pad
	
	l1        = Line2D([start+end_space,start+end_space+x_extent],[0,0], 
		           linewidth=linewidth, color=color, zorder=12, linestyle=linestyle)
	l1_top    = Line2D([start+end_space+x_extent,start+end_space+x_extent],[0,y_extent], 
		           linewidth=linewidth, color=color, zorder=12, linestyle=linestyle)
	l1_bottom = Line2D([start+end_space,start+end_space],[0,-y_extent], 
		           linewidth=linewidth, color=color, zorder=12, linestyle=linestyle)
	ax.add_line(l1)
	ax.add_line(l1_top)
	ax.add_line(l1_bottom)

	# White rectangle overlays backbone line
	p1 = Polygon([(start, y_extent), 
		          (start, -y_extent),
		          (end, -y_extent),
		          (end, y_extent)],
		          edgecolor=(1,1,1), facecolor=(1,1,1), linewidth=linewidth, zorder=11, 
		          path_effects=[Stroke(joinstyle="miter")]) # This is a work around for matplotlib < 1.4.0)		

	ax.add_patch(p1)

	if opts != None and 'label' in opts.keys():
		write_label(ax, opts['label'], final_start+((final_end-final_start)/2.0), opts=opts)

	return final_start, final_end

def sbol_user_defined  (ax, type, num, start, end, prev_end, scale, linewidth, opts):
	""" Built-in SBOL user-defined element renderer.
	"""
	# Default options
	color = (0,0,0)
	start_pad = 2.0
	end_pad = 2.0
	x_extent = 12.0
	y_extent = 3.0
	linestyle = '-'
	fill_color = (1,1,1)
	# Reset defaults if provided
	if opts != None:
		if 'color' in opts.keys():
			color = opts['color']
		if 'fill_color' in opts.keys():
			fill_color = opts['fill_color']
		if 'start_pad' in opts.keys():
			start_pad = opts['start_pad']
		if 'end_pad' in opts.keys():
			end_pad = opts['end_pad']
		if 'x_extent' in opts.keys():
			x_extent = opts['x_extent']
		if 'y_extent' in opts.keys():
			y_extent = opts['y_extent']
		if 'linestyle' in opts.keys():
			linestyle = opts['linestyle']
		if 'linewidth' in opts.keys():
			linewidth = opts['linewidth']
		if 'scale' in opts.keys():
			scale = opts['scale']
	# Check direction add start padding
	final_end = end
	final_start = prev_end

	start = prev_end+start_pad
	end = start+x_extent
	final_end = end+end_pad
	
	#white rectangle overlays backbone line
	p1 = Polygon([(start, y_extent), 
		          (start, -y_extent),
		          (start+x_extent, -y_extent),
		          (start+x_extent, y_extent)],
		          edgecolor=(0.0,0.0,0.0), facecolor=color, linewidth=linewidth, zorder=11, 
		          #edgecolor=color, facecolor=fill_color, linewidth=linewidth, zorder=11, 
		          path_effects=[Stroke(joinstyle="miter")]) # This is a work around for matplotlib < 1.4.0)		

	ax.add_patch(p1)
	
	if opts != None and 'label' in opts.keys():
		if final_start > final_end:
			write_label(ax, opts['label'], final_end+((final_start-final_end)/2.0), opts=opts)
		else:
			write_label(ax, opts['label'], final_start+((final_end-final_start)/2.0), opts=opts)

	if final_start > final_end:
		return prev_end, final_start
	else:
		return prev_end, final_end

def sbol_signature  (ax, type, num, start, end, prev_end, scale, linewidth, opts):
	""" Built-in SBOL signature renderer.
	"""
	# Default options
	color = (0,0,0)
	start_pad = 2.0
	end_pad = 2.0
	x_extent = 12.0
	y_extent = 3.0
	linestyle = '-'
	fill_color = (1,1,1)
	# Reset defaults if provided
	if opts != None:
		if 'color' in opts.keys():
			color = opts['color']
		if 'fill_color' in opts.keys():
			fill_color = opts['fill_color']
		if 'start_pad' in opts.keys():
			start_pad = opts['start_pad']
		if 'end_pad' in opts.keys():
			end_pad = opts['end_pad']
		if 'x_extent' in opts.keys():
			x_extent = opts['x_extent']
		if 'y_extent' in opts.keys():
			y_extent = opts['y_extent']
		if 'linestyle' in opts.keys():
			linestyle = opts['linestyle']
		if 'linewidth' in opts.keys():
			linewidth = opts['linewidth']
		if 'scale' in opts.keys():
			scale = opts['scale']
	
	direction = 'F'
	if start > end:
		direction = 'R'
		temp_end = end
		end = start
		start = temp_end

	final_end = prev_end
	final_start = prev_end

	if direction == 'F':
		final_start = prev_end
		start = prev_end+start_pad
		end = start+x_extent
		final_end = end+end_pad
	else:
		final_start = prev_end
		end = prev_end+end_pad
		start = end+x_extent
		final_start = start+start_pad

	indent_fac = (y_extent*2.0)*0.3
	cross_width = (y_extent*2.0)*0.7

	if direction == 'F':
		p1 = Polygon([(start, y_extent), 
			          (start, -y_extent),
			          (start+x_extent, -y_extent),
			          (start+x_extent, y_extent)],
			          edgecolor=color, facecolor=fill_color, linewidth=linewidth, zorder=11, 
		          path_effects=[Stroke(joinstyle="miter")]) # This is a work around for matplotlib < 1.4.0)		
		ax.add_patch(p1)
		top1x = start + indent_fac
		top1y = y_extent - indent_fac
		top2x = start + cross_width
		top2y = y_extent - indent_fac
		bot1x = start + indent_fac
		bot1y = -y_extent + indent_fac
		bot2x = start + cross_width
		bot2y = -y_extent + indent_fac
		lcross1 = Line2D([top1x,bot2x],[top1y,bot2y], 
		                  linewidth=linewidth, color=color, zorder=12, linestyle=linestyle)
		lcross2 = Line2D([top2x,bot1x],[top2y,bot1y], 
		                  linewidth=linewidth, color=color, zorder=12, linestyle=linestyle)
		ax.add_line(lcross1)
		ax.add_line(lcross2)
		lsign = Line2D([bot2x+indent_fac,end-indent_fac],[-y_extent+indent_fac,-y_extent+indent_fac], 
		               linewidth=linewidth, color=color, zorder=12, linestyle=linestyle)
		ax.add_line(lsign)
	else:
		p1 = Polygon([(start, y_extent), 
			          (start, -y_extent),
			          (start-x_extent, -y_extent),
			          (start-x_extent, y_extent)],
			          edgecolor=color, facecolor=fill_color, linewidth=linewidth, zorder=11, 
		          path_effects=[Stroke(joinstyle="miter")]) # This is a work around for matplotlib < 1.4.0)
		ax.add_patch(p1)
		top1x = start - indent_fac
		top1y = y_extent - indent_fac
		top2x = start - cross_width
		top2y = y_extent - indent_fac
		bot1x = start - indent_fac
		bot1y = -y_extent + indent_fac
		bot2x = start - cross_width
		bot2y = -y_extent + indent_fac
		lcross1 = Line2D([top1x,bot2x],[top1y,bot2y], 
		                  linewidth=linewidth, color=color, zorder=12, linestyle=linestyle)
		lcross2 = Line2D([top2x,bot1x],[top2y,bot1y], 
		                  linewidth=linewidth, color=color, zorder=12, linestyle=linestyle)
		ax.add_line(lcross1)
		ax.add_line(lcross2)
		lsign = Line2D([bot2x-indent_fac,end+indent_fac],[y_extent-indent_fac,y_extent-indent_fac], 
		               linewidth=linewidth, color=color, zorder=12, linestyle=linestyle)
		ax.add_line(lsign)

	if opts != None and 'label' in opts.keys():
		if final_start > final_end:
			write_label(ax, opts['label'], final_end+((final_start-final_end)/2.0), opts=opts)
		else:
			write_label(ax, opts['label'], final_start+((final_end-final_start)/2.0), opts=opts)

	if final_start > final_end:
		return prev_end, final_start
	else:
		return prev_end, final_end

def sbol_restriction_site (ax, type, num, start, end, prev_end, scale, linewidth, opts):
	""" Built-in SBOL restriction site renderer.
	"""
	# Default options
	color = (0,0,0)
	start_pad = 2.0
	end_pad = 2.0
	y_extent = 4.0
	linestyle = '-'
	# Reset defaults if provided
	if opts != None:
		if 'color' in opts.keys():
			color = opts['color']
		if 'start_pad' in opts.keys():
			start_pad = opts['start_pad']
		if 'end_pad' in opts.keys():
			end_pad = opts['end_pad']
		if 'y_extent' in opts.keys():
			y_extent = opts['y_extent']
		if 'linestyle' in opts.keys():
			linestyle = opts['linestyle']
		if 'linewidth' in opts.keys():
			linewidth = opts['linewidth']
		if 'scale' in opts.keys():
			scale = opts['scale']
	# Check direction add start padding
	final_end = end
	final_start = prev_end

	start = prev_end+start_pad
	end = start + linewidth
	final_end = end+end_pad
	
	l1    = Line2D([start,start],[-y_extent,y_extent], 
		           linewidth=linewidth, color=color, zorder=12, linestyle=linestyle)
	ax.add_line(l1)

	if opts != None and 'label' in opts.keys():
		if final_start > final_end:
			write_label(ax, opts['label'], final_end+((final_start-final_end)/2.0), opts=opts)
		else:
			write_label(ax, opts['label'], final_start+((final_end-final_start)/2.0), opts=opts)

	if final_start > final_end:
		return prev_end, final_start
	else:
		return prev_end, final_end


def sbol_spacer (ax, type, num, start, end, prev_end, scale, linewidth, opts):
	""" Built-in SBOL spacer renderer.
	"""
	# Default options
	color = (0,0,0)
	start_pad = 2.0
	end_pad = 2.0
	x_extent = 6.0
	y_extent = 6.0
	linestyle = '-'
	# Reset defaults if provided
	if opts != None:
		if 'color' in opts.keys():
			color = opts['color']
		if 'start_pad' in opts.keys():
			start_pad = opts['start_pad']
		if 'end_pad' in opts.keys():
			end_pad = opts['end_pad']
		if 'x_extent' in opts.keys():
			x_extent = opts['x_extent']
		if 'y_extent' in opts.keys():
			y_extent = opts['y_extent']
		if 'linestyle' in opts.keys():
			linestyle = opts['linestyle']
		if 'linewidth' in opts.keys():
			linewidth = opts['linewidth']
		if 'scale' in opts.keys():
			scale = opts['scale']
	# Check direction add start padding
	final_end = end
	final_start = prev_end
	
	start = prev_end+start_pad
	end = start+x_extent
	final_end = end+end_pad
	rbs_center = (start+((end-start)/2.0),0)
	center_x = start+(end-start)/2.0
	radius = x_extent/2

	delta = radius - 0.5 * radius * math.sqrt(2)

	l1 = Line2D([start+delta,end-delta],[radius-delta,-1*radius+delta], 
		        linewidth=linewidth, color=color, zorder=12, linestyle=linestyle)
	l2 = Line2D([start+delta,end-delta],[-1*radius+delta,radius-delta], 
		        linewidth=linewidth, color=color, zorder=12, linestyle=linestyle)
	c1 = Circle(rbs_center, x_extent/2.0, linewidth=linewidth, edgecolor=color, 
		        facecolor=(1,1,1), zorder=12)
	
	ax.add_patch(c1)
	ax.add_line(l1)
	ax.add_line(l2)

	if opts != None and 'label' in opts.keys():
		if final_start > final_end:
			write_label(ax, opts['label'], final_end+((final_start-final_end)/2.0), opts=opts)
		else:
			write_label(ax, opts['label'], final_start+((final_end-final_start)/2.0), opts=opts)

	if final_start > final_end:
		return prev_end, final_start
	else:
		return prev_end, final_end


def sbol_origin (ax, type, num, start, end, prev_end, scale, linewidth, opts):
	""" Built-in SBOL origin renderer.
	"""
	# Default options
	color = (0,0,0)
	start_pad = 2.0
	end_pad = 2.0
	x_extent = 10.0
	y_extent = 10.0
	linestyle = '-'
	# Reset defaults if provided
	if opts != None:
		if 'color' in opts.keys():
			color = opts['color']
		if 'start_pad' in opts.keys():
			start_pad = opts['start_pad']
		if 'end_pad' in opts.keys():
			end_pad = opts['end_pad']
		if 'x_extent' in opts.keys():
			x_extent = opts['x_extent']
		if 'y_extent' in opts.keys():
			y_extent = opts['y_extent']
		if 'linestyle' in opts.keys():
			linestyle = opts['linestyle']
		if 'linewidth' in opts.keys():
			linewidth = opts['linewidth']
		if 'scale' in opts.keys():
			scale = opts['scale']
	# Check direction add start padding
	final_end = end
	final_start = prev_end
	
	start = prev_end+start_pad
	end = start+x_extent
	final_end = end+end_pad
	ori_center = (start+((end-start)/2.0),0)
	
	c1 = Circle(ori_center, x_extent/2.0, linewidth=linewidth, edgecolor=color, 
		        facecolor=(1,1,1), zorder=12)
	
	ax.add_patch(c1)
	
	if opts != None and 'label' in opts.keys():
		if final_start > final_end:
			write_label(ax, opts['label'], final_end+((final_start-final_end)/2.0), opts=opts)
		else:
			write_label(ax, opts['label'], final_start+((final_end-final_start)/2.0), opts=opts)

	if final_start > final_end:
		return prev_end, final_start
	else:
		return prev_end, final_end

def sbol_operator (ax, type, num, start, end, prev_end, scale, linewidth, opts):
	""" Built-in SBOL operator renderer.
	"""
	# Default options
	color = (0,0,0)
	start_pad = 2.0
	end_pad = 2.0
	x_extent = 6.0
	y_extent = 3.0
	linestyle = '-'
	# Reset defaults if provided
	if opts != None:
		if 'color' in opts.keys():
			color = opts['color']
		if 'start_pad' in opts.keys():
			start_pad = opts['start_pad']
		if 'end_pad' in opts.keys():
			end_pad = opts['end_pad']
		if 'x_extent' in opts.keys():
			x_extent = opts['x_extent']
		if 'y_extent' in opts.keys():
			y_extent = opts['y_extent']
		if 'linestyle' in opts.keys():
			linestyle = opts['linestyle']
		if 'linewidth' in opts.keys():
			linewidth = opts['linewidth']
		if 'scale' in opts.keys():
			scale = opts['scale']
	# Check direction add start padding
	final_end = end
	final_start = prev_end

	start = prev_end+start_pad
	end = start+x_extent
	final_end = end+end_pad
	
	#white rectangle overlays backbone line
	p1 = Polygon([(start, y_extent), 
		          (start, -y_extent),
		          (start+x_extent, -y_extent),
		          (start+x_extent, y_extent)],
		          edgecolor=(0,0,0), facecolor=(1,1,1), linewidth=linewidth, zorder=11, 
		          path_effects=[Stroke(joinstyle="miter")]) # This is a work around for matplotlib < 1.4.0)		

	ax.add_patch(p1)
	
	if opts != None and 'label' in opts.keys():
		if final_start > final_end:
			write_label(ax, opts['label'], final_end+((final_start-final_end)/2.0), opts=opts)
		else:
			write_label(ax, opts['label'], final_start+((final_end-final_start)/2.0), opts=opts)

	if final_start > final_end:
		return prev_end, final_start
	else:
		return prev_end, final_end

def sbol_insulator (ax, type, num, start, end, prev_end, scale, linewidth, opts):
	""" Built-in SBOL insulator renderer.
	"""
	# Default options
	color = (0,0,0)
	start_pad = 2.0
	end_pad = 2.0
	x_extent = 8.0
	y_extent = 4.0
	linestyle = '-'
	# Reset defaults if provided
	if opts != None:
		if 'color' in opts.keys():
			color = opts['color']
		if 'start_pad' in opts.keys():
			start_pad = opts['start_pad']
		if 'end_pad' in opts.keys():
			end_pad = opts['end_pad']
		if 'x_extent' in opts.keys():
			x_extent = opts['x_extent']
		if 'y_extent' in opts.keys():
			y_extent = opts['y_extent']
		if 'linestyle' in opts.keys():
			linestyle = opts['linestyle']
		if 'linewidth' in opts.keys():
			linewidth = opts['linewidth']
		if 'scale' in opts.keys():
			scale = opts['scale']
	
	# Check direction add start padding
	final_end = end
	final_start = prev_end
	start = prev_end+start_pad
	end = start+x_extent
	final_end = end+end_pad
	
	#white rectangle overlays backbone line
	p1 = Polygon([(start, y_extent), 
		          (start, -y_extent),
		          (start+x_extent, -y_extent),
		          (start+x_extent, y_extent)],
		          edgecolor=(0,0,0), facecolor=(1,1,1), linewidth=linewidth, zorder=11, 
		          path_effects=[Stroke(joinstyle="miter")]) # This is a work around for matplotlib < 1.4.0)		

	bits = 5.0
	gap_size = ((end-start)/bits)
	x_inset_start = start + gap_size
	x_inset_end = start + ((bits-1.0)*gap_size)

	# Inside rectangle
	p2 = Polygon([(x_inset_start,  y_extent-gap_size), 
		          (x_inset_start, -y_extent+gap_size),
		          (x_inset_end, -y_extent+gap_size),
		          (x_inset_end,  y_extent-gap_size)],
		          edgecolor=(0,0,0), facecolor=(1,1,1), linewidth=linewidth, zorder=12, 
		          path_effects=[Stroke(joinstyle="miter")]) # This is a work around for matplotlib < 1.4.0)		

	ax.add_patch(p1)
	ax.add_patch(p2)
	
	if opts != None and 'label' in opts.keys():
		if final_start > final_end:
			write_label(ax, opts['label'], final_end+((final_start-final_end)/2.0), opts=opts)
		else:
			write_label(ax, opts['label'], final_start+((final_end-final_start)/2.0), opts=opts)

	if final_start > final_end:
		return prev_end, final_start
	else:
		return prev_end, final_end

# Not used at present
def temporary_repressor (ax, type, num, start, end, prev_end, scale, linewidth, opts):
	# Default options
	color = (0.7,0.7,0.7)
	start_pad = 2.0
	end_pad = 2.0
	y_extent = 10
	x_extent = 10
	arrowhead_height = 2
	arrowhead_length = 4
	# Reset defaults if provided
	if opts != None:
		if 'color' in opts.keys():
			color = opts['color']
		if 'start_pad' in opts.keys():
			start_pad = opts['start_pad']
		if 'end_pad' in opts.keys():
			end_pad = opts['end_pad']
		if 'y_extent' in opts.keys():
			y_extent = opts['y_extent']
		if 'x_extent' in opts.keys():
			x_extent = opts['x_extent']
		if 'arrowhead_height' in opts.keys():
			arrowhead_height = opts['arrowhead_height']
		if 'arrowhead_length' in opts.keys():
			arrowhead_length = opts['arrowhead_length']
		if 'linewidth' in opts.keys():
			linewidth = opts['linewidth']
		if 'scale' in opts.keys():
			scale = opts['scale']
	# Check direction add start padding
	dir_fac = 1.0
	final_end = end
	final_start = prev_end
	if start > end:
		dir_fac = -1.0
		start = prev_end+end_pad+x_extent
		end = prev_end+end_pad
		final_end = start+start_pad
	else:
		start = prev_end+start_pad
		end = start+x_extent
		final_end = end+end_pad
	
	e1center = (start+((end-start)/2.0),0)
	e2center = (start+((end-start)/2.0)+x_extent/3.75,0)

	e1 = Ellipse(e1center, y_extent/2, y_extent, edgecolor=(0,0,0), facecolor=color, 
				linewidth=linewidth, fill=True, zorder=12)
	e2 = Ellipse(e2center, y_extent/2, y_extent, edgecolor=(0,0,0), facecolor=color, 
				linewidth=linewidth, fill=True, zorder=11)

	ax.add_patch(e1)
	ax.add_patch(e2)

	if final_start > final_end:
		return prev_end, final_start
	else:
		return prev_end, final_end

###############################################################################
# Regulation renderers
###############################################################################

def repress (ax, type, num, from_part, to_part, scale, linewidth, arc_height_index, opts):
	""" Standard repression regulation renderer.
	"""
	regulation(ax, type, num, from_part, to_part, scale, linewidth, arc_height_index, opts)

def induce (ax, type, num, from_part, to_part, scale, linewidth, arc_height_index, opts):
	""" Standard induction regulation renderer.
	"""
	regulation(ax, type, num, from_part, to_part, scale, linewidth, arc_height_index, opts)

def regulation (ax, type, num, from_part, to_part, scale, linewidth, arc_height_index, opts):
	""" General function for drawing regulation arcs.
	"""

	color = (0.0,0.0,0.0)
	arrowhead_length = 4
	linestyle = '-'
	
	#Warning: change these to params instead of hard-coded numbers
	arcHeight = 15 + arc_height_index*5
	startHeight = 10
	
	# Reset defaults if provided
	if opts != None:
		if 'arrowhead_length' in opts.keys():
			arrowhead_length = opts['arrowhead_length']
		if 'linestyle' in opts.keys():
			linestyle = opts['linestyle']
		if 'linewidth' in opts.keys():
			linewidth = opts['linewidth']
		if 'color' in opts.keys():
			color = opts['color']
	
	start = (from_part['start'] + from_part['end']) / 2
	end   = (to_part['start']   + to_part['end']) / 2

	top = arcHeight;
	base = startHeight;
	indHeight = arrowhead_length
	
	if(to_part['fwd'] == False):
		base = -1*startHeight
		top  = -1*arcHeight
		indHeight = -1*arrowhead_length

	line_away   = Line2D([start,start],[base/1.2,top], 
		        linewidth=linewidth, color=color, zorder=12, linestyle=linestyle)
	line_across = Line2D([start,end],[top,top], 
		        linewidth=linewidth, color=color, zorder=12, linestyle=linestyle)
	line_toward = Line2D([end,end],[top,base*1.5], 
		        linewidth=linewidth, color=color, zorder=12, linestyle=linestyle)
	line_rep    = Line2D([end-arrowhead_length,end+arrowhead_length],[base*1.5,base*1.5], 
		        linewidth=linewidth, color=color, zorder=12, linestyle='-')
	line_ind1   = Line2D([end-arrowhead_length,end],[base*1.5+indHeight,base*1.5], 
		        linewidth=linewidth, color=color, zorder=12, linestyle='-')
	line_ind2    = Line2D([end+arrowhead_length,end],[base*1.5+indHeight,base*1.5], 
		        linewidth=linewidth, color=color, zorder=12, linestyle='-')

	ax.add_line(line_away)
	ax.add_line(line_across)
	ax.add_line(line_toward)

	if(type == 'Repression'):
		ax.add_line(line_rep)

	if(type == 'Activation'):
		ax.add_line(line_ind1)
		ax.add_line(line_ind2)

###############################################################################
# Trace Icon Renderers (icon width corrisponds to trace data)
###############################################################################

def trace_promoter (ax, type, num, start_bp, end_bp, prev_end, scale, linewidth, opts):
	""" Built-in trace-based promoter renderer.
	"""
	# Default options
	color = (0.0,0.0,1.0)
	y_extent = 6
	x_extent = 30
	arrowhead_height = 0.5
	arrowhead_length = 15
	highlight_y_extent = 0.8
	# Reset defaults if provided
	if opts != None:
		if 'color' in opts.keys():
			color = opts['color']
		if 'y_extent' in opts.keys():
			y_extent = opts['y_extent']
		if 'x_extent' in opts.keys():
			x_extent = opts['x_extent']
		if 'arrowhead_height' in opts.keys():
			arrowhead_height = opts['arrowhead_height']
		if 'arrowhead_length' in opts.keys():
			arrowhead_length = opts['arrowhead_length']
		if 'highlight_y_extent' in opts.keys():
			highlight_y_extent = opts['highlight_y_extent']
		if 'linewidth' in opts.keys():
			linewidth = opts['linewidth']
		if 'scale' in opts.keys():
			scale = opts['scale']
	# Check direction add start padding
	dir_fac = 1.0
	if start_bp > end_bp:
		dir_fac = -1.0
	# Draw the promoter symbol
	l1 = Line2D([start_bp,start_bp],[0,dir_fac*y_extent], linewidth=linewidth, 
		        color=color, zorder=14)
	l2 = Line2D([start_bp,start_bp+dir_fac*x_extent*scale-dir_fac*arrowhead_length*0.5*scale],
                [dir_fac*y_extent,dir_fac*y_extent], linewidth=linewidth, 
                color=color, zorder=14)
	ax.add_line(l1)
	ax.add_line(l2)
	p1 = Polygon([(start_bp+dir_fac*x_extent*scale-dir_fac*arrowhead_length*scale, 
		           dir_fac*y_extent+(arrowhead_height)), 
		          (start_bp+dir_fac*(x_extent*scale), dir_fac*y_extent),
		          (start_bp+dir_fac*x_extent*scale-dir_fac*arrowhead_length*scale, 
		           dir_fac*y_extent-(arrowhead_height))],
		          facecolor=color, edgecolor=color, linewidth=linewidth, zorder=14, 
		          path_effects=[Stroke(joinstyle="miter")]) # This is a work around for matplotlib < 1.4.0)
	ax.add_patch(p1)
	# Shade the promoter area (normally smaller than symbol extent)
 	p2 = Polygon([(start_bp, -highlight_y_extent), 
 		          (start_bp, highlight_y_extent),
 		          (end_bp, highlight_y_extent),
 		          (end_bp, -highlight_y_extent)], facecolor=color, edgecolor=color, linewidth=linewidth, zorder=14, 
		          path_effects=[Stroke(joinstyle="miter")]) # This is a work around for matplotlib < 1.4.0)
	ax.add_patch(p2)
	if opts != None and 'label' in opts.keys():
		if start_bp > end_bp:
			write_label(ax, opts['label'], end_bp+((start_bp-end_bp)/2.0), opts=opts)
		else:
			write_label(ax, opts['label'], start_bp+((end_bp-start_bp)/2.0), opts=opts)
	if start_bp > end_bp:
		return end_bp, start_bp
	else:
		return start_bp, end_bp

def trace_rbs (ax, type, num, start_bp, end_bp, prev_end, scale, linewidth, opts):
	""" Built-in trace-based ribosome binding site renderer.
	"""
	# Default options
	color = (0.16,0.68,0.15)
	y_extent = 3.5
	x_extent = 10
	highlight_y_extent = 0.8
	# Reset defaults if provided
	if opts != None:
		if 'color' in opts.keys():
			color = opts['color']
		if 'y_extent' in opts.keys():
			y_extent = opts['y_extent']
		if 'x_extent' in opts.keys():
			x_extent = opts['x_extent']
		if 'highlight_y_extent' in opts.keys():
			highlight_y_extent = opts['highlight_y_extent']
		if 'linewidth' in opts.keys():
			linewidth = opts['linewidth']
		if 'scale' in opts.keys():
			scale = opts['scale']
	# Check direction add start padding
	dir_fac = 1.0
	if start_bp > end_bp:
		dir_fac = -1.0
	# Draw the RBS symbol
	l1 = Line2D([start_bp,start_bp],[0,dir_fac*y_extent], linewidth=linewidth, color=color, zorder=14)
	ax.add_line(l1)
	c1 = Ellipse((start_bp,dir_fac*y_extent),width=(x_extent*scale),height=y_extent*0.4,color=color, zorder=14)
	ax.add_artist(c1)
 	# Shade the promoter area (normally smaller than symbol extent)
 	p2 = Polygon([(start_bp, -highlight_y_extent), 
 		          (start_bp, highlight_y_extent),
 		          (end_bp, highlight_y_extent),
 		          (end_bp, -highlight_y_extent)], facecolor=color, edgecolor=color, linewidth=linewidth, zorder=13, 
		          path_effects=[Stroke(joinstyle="miter")]) # This is a work around for matplotlib < 1.4.0)
	ax.add_patch(p2)
	if opts != None and 'label' in opts.keys():
		if start_bp > end_bp:
			write_label(ax, opts['label'], end_bp+((start_bp-end_bp)/2.0), opts=opts)
		else:
			write_label(ax, opts['label'], start_bp+((end_bp-start_bp)/2.0), opts=opts)
	if start_bp > end_bp:
		return end_bp, start_bp
	else:
		return start_bp, end_bp

def trace_cds (ax, type, num, start_bp, end_bp, prev_end, scale, linewidth, opts):
	""" Built-in trace-based coding sequence renderer.
	"""
	# Default options
	color = (0.7,0.7,0.7)
	hatch = ''
	y_extent = 1.5
	arrowhead_height = 1
	arrowhead_length = 30
	# Reset defaults if provided
	if opts != None:
		if 'color' in opts.keys():
			color = opts['color']
		if 'hatch' in opts.keys():
			hatch = opts['hatch']
		if 'y_extent' in opts.keys():
			y_extent = opts['y_extent']
		if 'arrowhead_height' in opts.keys():
			arrowhead_height = opts['arrowhead_height']
		if 'arrowhead_length' in opts.keys():
			arrowhead_length = opts['arrowhead_length']
		if 'linewidth' in opts.keys():
			linewidth = opts['linewidth']
		if 'scale' in opts.keys():
			scale = opts['scale']
	# Check direction add start padding
	dir_fac = 1.0
	if start_bp > end_bp:
		dir_fac = -1.0
	# Draw the CDS symbol
	p1 = Polygon([(start_bp, y_extent), 
		          (start_bp, -y_extent),
		          (end_bp-dir_fac*arrowhead_length*scale, -y_extent),
		          (end_bp-dir_fac*arrowhead_length*scale, -y_extent-arrowhead_height),
		          (end_bp, 0),
		          (end_bp-dir_fac*arrowhead_length*scale, y_extent+arrowhead_height),
		          (end_bp-dir_fac*arrowhead_length*scale, y_extent)],
		          edgecolor=(0.0,0.0,0.0), facecolor=color, linewidth=linewidth, 
		          hatch=hatch, zorder=15, 
		          path_effects=[Stroke(joinstyle="miter")]) # This is a work around for matplotlib < 1.4.0)
	ax.add_patch(p1)
	if opts != None and 'label' in opts.keys():
		if start_bp > end_bp:
			write_label(ax, opts['label'], end_bp+((start_bp-end_bp)/2.0), opts=opts)
		else:
			write_label(ax, opts['label'], start_bp+((end_bp-start_bp)/2.0), opts=opts)
	if start_bp > end_bp:
		return end_bp, start_bp
	else:
		return start_bp, end_bp

def trace_terminator (ax, type, num, start_bp, end_bp, prev_end, scale, linewidth, opts):
	""" Built-in trace-based terminator renderer.
	"""
	# Default options
	color = (1.0,0.0,0.0)
	y_extent = 3.5
	x_extent = 10
	highlight_y_extent = 0.8
	# Reset defaults if provided
	if opts != None:
		if 'color' in opts.keys():
			color = opts['color']
		if 'y_extent' in opts.keys():
			y_extent = opts['y_extent']
		if 'x_extent' in opts.keys():
			x_extent = opts['x_extent']
		if 'highlight_y_extent' in opts.keys():
			highlight_y_extent = opts['highlight_y_extent']
		if 'linewidth' in opts.keys():
			linewidth = opts['linewidth']
		if 'scale' in opts.keys():
			scale = opts['scale']
	# Check direction add start padding
	dir_fac = 1.0
	if start_bp > end_bp:
		dir_fac = -1.0
	# Draw the terminator symbol
	l1 = Line2D([start_bp,start_bp],[0,dir_fac*y_extent], linewidth=linewidth, color=color, zorder=8)
	l2 = Line2D([start_bp-(x_extent*scale),start_bp+(x_extent*scale)],[dir_fac*y_extent,dir_fac*y_extent], linewidth=linewidth, color=color, zorder=14)
	ax.add_line(l1)
	ax.add_line(l2)
	# Shade the terminator area (normally smaller than symbol extent)
 	p2 = Polygon([(start_bp, -highlight_y_extent), 
 		          (start_bp, highlight_y_extent),
 		          (end_bp, highlight_y_extent),
 		          (end_bp, -highlight_y_extent)], facecolor=color, edgecolor=color, linewidth=linewidth, zorder=13, 
		          path_effects=[Stroke(joinstyle="miter")]) # This is a work around for matplotlib < 1.4.0)
	ax.add_patch(p2)
	if opts != None and 'label' in opts.keys():
		if start_bp > end_bp:
			write_label(ax, opts['label'], end_bp+((start_bp-end_bp)/2.0), opts=opts)
		else:
			write_label(ax, opts['label'], start_bp+((end_bp-start_bp)/2.0), opts=opts)
	if start_bp > end_bp:
		return end_bp, start_bp
	else:
		return start_bp, end_bp

###############################################################################
# The DNA renderer
###############################################################################

class DNARenderer:
	""" Class defining the DNA rendering funtionality.
	"""

	# Standard part types
	STD_PART_TYPES = ['Promoter',
	                  'CDS',
                      'Terminator',
                      'RBS',
                      'Scar',
                      'Spacer',
                      'Ribozyme',
                      'Ribonuclease',
                      'ProteinStability',
                      'Protease',
                      'Operator',
                      'Origin',
                      'Insulator']

	# Standard regulatory types
	STD_REG_TYPES = ['Repression',
	                 'Activation']

	def __init__(self, scale=1.0, linewidth=1.0, 
		         backbone_pad_left=0.0, backbone_pad_right=0.0):
		""" Constructor to generate an empty DNARenderer.

		Parameters
	    ----------
	    scale : float (default=1.0)
	        A scaling factor for the plot. Only used if rendering traces.

	    linewidth : float (default=1.0)
	    	The default linewidth for all part drawing.

	    backbone_pad_left : float (default=0.0)
	    	Padding to add to the left side of the backbone.

	    backbone_pad_right : float (default=0.0)
	    	Padding to add to the left side of the backbone.
		"""
		self.scale = scale
		self.linewidth = linewidth
		self.backbone_pad_left = backbone_pad_left
		self.backbone_pad_right = backbone_pad_right
		self.reg_height = 15

	def SBOL_part_renderers (self):
		""" Return dictionary of all standard built-in SBOL part renderers.
		"""
		return {
			'Promoter'         :sbol_promoter, 
			'CDS'              :sbol_cds, 
			'Terminator'       :sbol_terminator,
			'RBS'              :sbol_rbs,
			'Scar'             :sbol_scar,
			'Spacer'	       :sbol_spacer,
			'EmptySpace'	   :sbol_empty_space,
			'Ribozyme'         :sbol_ribozyme,
			'Ribonuclease'     :sbol_ribonuclease,
			'ProteinStability' :sbol_protein_stability,
			'Protease'         :sbol_protease,
			'Operator'         :sbol_operator,
			'Origin'           :sbol_origin,
			'Insulator'        :sbol_insulator,
			'5Overhang'        :sbol_5_overhang,
			'3Overhang'        :sbol_3_overhang,
			'RestrictionSite'  :sbol_restriction_site,
			'BluntRestrictionSite'   :sbol_blunt_restriction_site,
			'PrimerBindingSite'      :sbol_primer_binding_site,
			'5StickyRestrictionSite' :sbol_5_sticky_restriction_site,
			'3StickyRestrictionSite' :sbol_3_sticky_restriction_site,
			'UserDefined'      :sbol_user_defined,
			'Signature'        :sbol_signature}

	def trace_part_renderers (self):
		""" Return dictionary of all standard built-in trace part renderers.
		"""
		return {
			'Promoter'         :trace_promoter, 
			'CDS'              :trace_cds, 
			'Terminator'       :trace_terminator,
			'RBS'              :trace_rbs} 

	def std_reg_renderers (self):
		""" Return dictionary of all standard built-in regulation renderers.
		"""
		return {
			'Repression' :repress, 
			'Activation' :induce}

	def renderDNA(self, ax, parts, part_renderers, regs=None, reg_renderers=None):
		""" Render the parts on the DNA and regulation.

		Parameters
	    ----------
	    ax : matplotlib.axes
	        Axes to draw the design to.

	    parts : list(dict)
	    	The design to draw. This is a list of dicts, where each dict relates to
	    	a part and must contain the following keys:
	    	- name (string)
	    	- type (string)  
	    	- fwd (bool)
	    	These will then be drawn in accordance with the renders selected

	    part_renderers : dict(functions)
	    	Dict of functions where the key in the part type and the dictionary returns
	    	the function to be used to draw that part type.

	    regs : list(dict) (default=None)
	    	Regulation present in the design. This is a list of dicts, where each dict
	    	relates to a single regulation arc and must contain the following keys:
	    	- type (string)
	    	- from_part (int)  
	    	- to_part (int)
	    	These will then be drawn in accordance with the renders selected.

	    reg_renderers : dict(functions) (default=None)
	    	Dict of functions where the key in the regulation type and the dictionary 
	    	returns the function to be used to draw that regulation type.

	    Returns
	    -------
	    start : float
	    	The x-point in the axis space that drawing begins.

	    end : float
	    	The x-point in the axis space that drawing ends.
		"""
		# Update the matplotlib rendering default for drawing the parts (we want mitered edges)
		matplotlib.rcParams['lines.dash_joinstyle']  = 'miter'
		matplotlib.rcParams['lines.dash_capstyle']   = 'butt'
		matplotlib.rcParams['lines.solid_joinstyle'] = 'miter'
		matplotlib.rcParams['lines.solid_capstyle']  = 'projecting'
		# Plot the parts to the axis
		part_num = 0
		prev_end = 0
		first_start = 0
		first_part = True
		for part in parts:
			keys = part.keys()
			# Check the part has minimal details required
			if 'type' in keys:
				if 'fwd' not in keys:
					part['fwd'] = 'True'
				if 'start' not in keys:
					if part['fwd'] == True:
						part['start'] = part_num
					else:
						part['start'] = part_num+1
				if 'end' not in keys:
					if part['fwd'] == True:
						part['end'] = part_num+1
					else:
						part['end'] = part_num
				# Extract custom part options (if available)
				part_opts = None
				if 'opts' in part.keys():
					part_opts = part['opts']
				# Use the correct renderer
				if 'renderer' in part.keys():
					# Use custom renderer
					prev_start, prev_end = part['renderer'](ax, part['type'], part_num, 
						             part['start'], part['end'], prev_end,
						             self.scale, self.linewidth, 
						             opts=part_opts)

					#update start,end for regulation
					part['start'] = prev_start
					part['end'] = prev_end

					if first_part == True:
						first_start = prev_start
						first_part = False
				else:
					# Use standard renderer, if one exists
					if part['type'] in part_renderers.keys():
						prev_start, prev_end = part_renderers[part['type']](ax, 
							           part['type'], part_num, 
							           part['start'], part['end'], 
							           prev_end, self.scale, 
							           self.linewidth, opts=part_opts)
						
						#update start,end for regulation
						part['start'] = prev_start
						part['end'] = prev_end
						
						if first_part == True:
							first_start = prev_start
							first_part = False
			part_num += 1
		
		# first pass to get all of the arcranges
		if regs != None:

			for reg in regs:
				keys = reg.keys()

				# Check the part has minimal details required
				if 'type' in keys and 'from_part' in keys and 'to_part' in keys:
					# Extract custom part options (if available)

					reg_opts = None
					if 'opts' in reg.keys():
						reg_opts = reg['opts']
					
					if reg['type'] in reg_renderers.keys():
						
						##############################################################################
						arcstart = (reg['from_part']['start'] + reg['from_part']['end']) / 2
						arcend   = (reg['to_part']['start']   + reg['to_part']['end']) / 2
						arcrange = [arcstart,arcend]
						reg['arclength'] = math.fabs(arcstart-arcend)
						reg['arc_height_index'] = 1
						##############################################################################

			#sort regs by arc ranges from shortest to longest
			regs.sort(key=lambda x: x['arclength'], reverse=False)

			reg_num = 0
			pos_arc_ranges = [] # arc above DNA backbone if to_part is fwd
			neg_arc_ranges = [] # arc below DNA backbone if to_part is reverse
			current_max = 1

			# second pass to render all the arcs
			for reg in regs:
				keys = reg.keys()

				# Check the part has minimal details required
				if 'type' in keys and 'from_part' in keys and 'to_part' in keys:
					# Extract custom part options (if available)

					reg_opts = None
					if 'opts' in reg.keys():
						reg_opts = reg['opts']
					
					if reg['type'] in reg_renderers.keys():
						
						##############################################################################
						# arc height algorithm: greedy from left-to-right on DNA design
						
						arcstart = (reg['from_part']['start'] + reg['from_part']['end']) / 2
						arcend   = (reg['to_part']['start']   + reg['to_part']['end']) / 2
						
						arcmin = min(arcstart,arcend)
						arcmax = max(arcstart,arcend)
						arcrange = [arcmin,arcmax,reg['arc_height_index']]
						arc_height_index = 1
						
						# arc above if to_part is fwd
						if(reg['to_part']['fwd'] == True):
							# find max arc height index of ONLY the prior arcs that clash with the current arc
							current_max = 1
							for r in pos_arc_ranges:
								if  (arcrange[0] >= r[0] and arcrange[0] <= r[1]):
									if(r[2] > current_max):
										current_max = r[2]
								elif(arcrange[0] >= r[1] and arcrange[0] <= r[0]):
									if(r[2] > current_max):
										current_max = r[2]
								elif(arcrange[1] >= r[0] and arcrange[0] <= r[1]):
									if(r[2] > current_max):
										current_max = r[2]
								elif(arcrange[1] >= r[1] and arcrange[0] <= r[0]):
									if(r[2] > current_max):
										current_max = r[2]
									
							# if arcs cross over, increment the arc height index
							for r in pos_arc_ranges:
								if  (arcrange[0] >= r[0] and arcrange[0] <= r[1]):
									reg['arc_height_index'] = current_max + 1
									arcrange[2] = reg['arc_height_index']
								elif(arcrange[0] >= r[1] and arcrange[0] <= r[0]):
									reg['arc_height_index'] = current_max + 1
									arcrange[2] = reg['arc_height_index']
								elif(arcrange[1] >= r[0] and arcrange[0] <= r[1]):
									reg['arc_height_index'] = current_max + 1
									arcrange[2] = reg['arc_height_index']
								elif(arcrange[1] >= r[1] and arcrange[0] <= r[0]):
									reg['arc_height_index'] = current_max + 1
									arcrange[2] = reg['arc_height_index']
							pos_arc_ranges.append(arcrange)
						
						# arc below if to_part is reverse
						else:
							# find max arc height index
							current_max = 1
							for r in neg_arc_ranges:
								if  (arcrange[0] > r[0] and arcrange[0] < r[1]):
									if(r[2] > current_max):
										current_max = r[2]
								elif(arcrange[0] > r[1] and arcrange[0] < r[0]):
									if(r[2] > current_max):
										current_max = r[2]
								elif(arcrange[1] > r[0] and arcrange[0] < r[1]):
									if(r[2] > current_max):
										current_max = r[2]
								elif(arcrange[1] > r[1] and arcrange[0] < r[0]):
									if(r[2] > current_max):
										current_max = r[2]
							
							# if arcs cross over, increment the arc height index
							for r in neg_arc_ranges:
								if  (arcrange[0] > r[0] and arcrange[0] < r[1]):
									reg['arc_height_index'] = current_max + 1
									arcrange[2] = reg['arc_height_index']
								elif(arcrange[0] > r[1] and arcrange[0] < r[0]):
									reg['arc_height_index'] = current_max + 1
									arcrange[2] = reg['arc_height_index']
								elif(arcrange[1] > r[0] and arcrange[0] < r[1]):
									reg['arc_height_index'] = current_max + 1
									arcrange[2] = reg['arc_height_index']
								elif(arcrange[1] > r[1] and arcrange[0] < r[0]):
									reg['arc_height_index'] = current_max + 1
									arcrange[2] = reg['arc_height_index']
							neg_arc_ranges.append(arcrange)
						##############################################################################
						
						reg_renderers[reg['type']](ax, reg['type'], 
							           reg_num, reg['from_part'], 
							           reg['to_part'], self.scale, 
							           self.linewidth, reg['arc_height_index'], opts=reg_opts)
				reg_num += 1
		# Plot the backbone (z=1)
		l1 = Line2D([first_start-self.backbone_pad_left,prev_end+self.backbone_pad_right],[0,0], 
			        linewidth=self.linewidth, color=(0,0,0), zorder=10)
		ax.add_line(l1)
		return first_start, prev_end
