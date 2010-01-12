# visualize.py --- 
# 
# Filename: visualize.py
# Description: 
# Author: Vinay Jethava
# Maintainer: 
# Created: Wed Jan  6 14:50:27 2010 (+0530)
# Version: 
# Last-Updated: Fri Jan  8 14:23:11 2010 (+0530)
#           By: Vinay Jethava
#     Update #: 131
# URL: 
# Keywords: 
# Compatibility: 
# 
# 

# Commentary: 
# 
# Script to convert the files to the required cytoscape format and# get the results out. 
# 
# 

# Change log:
# 
# 
# 
# 
# This program is free software; you can redistribute it and/or
# modify it under the terms of the GNU General Public License as
# published by the Free Software Foundation; either version 3, or
# (at your option) any later version.
# 
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
# General Public License for more details.
# 
# You should have received a copy of the GNU General Public License
# along with this program; see the file COPYING.  If not, write to
# the Free Software Foundation, Inc., 51 Franklin Street, Fifth
# Floor, Boston, MA 02110-1301, USA.
# 
# 

# Code:

from cytoscape import Cytoscape
from cytoscape.view import CyNetworkView
from cytoscape.util.export import BitmapExporter
import cytoscape.layout.CyLayouts as CyLayouts
import cytoscape.data.readers.CyAttributesReader as CyAttributesReader
import cytoscape.visual.VisualMappingManager as VisualMappingManager
import cytoscape.visual.VisualStyle as VisualStyle

import java.lang.Integer as Integer
import java.io.File as File
import java.io.FileOutputStream as FileOutputStream
import java.io.FileReader as FileReader 
# import java.lang.String as String

import random
import os

mainpath = '/home/vjethava/NETGEMM/results/'
# dnames = [ 'AE1', 'AE2', 'AE3', 'AE4', 'AE5', 'AE6', 'AE7', 'AE8', 'AN1', 'AN2', 'AN3', 'AN4', 'AN5', 'AN6', 'AN7', 'AN8']
# T = 8


dnames = ['AEJOINT'] #, 'REF', 'MUT']
T = 8
for l in range(len(dnames)):
    mypath = mainpath + dnames[l] + '/' 
    graphFile = mypath + 'graph.sif'
    graph = Cytoscape.createNetworkFromFile(graphFile, True)
    CyLayouts.getLayout("circular").doLayout()
    vs = VisualStyle('Solid') 
    Cytoscape.getCurrentNetworkView().applyVizmapper(vs)
    # Cytoscape.getCurrentNetworkView().redrawGraph(True)

    ############################################################
    # Write out the edge color images
    ############################################################
    for t in range(T):
        s = t+1
        colorFileName = mypath + 'edgeColor/' + 't' + str(s) + '.eda'
        imageFileName = mypath + 't' + str(s) + '.png' 
        nodeAttrFiles = []
        edgeAttrFiles = [colorFileName]
        Cytoscape.loadAttributes(nodeAttrFiles, edgeAttrFiles)
        Cytoscape.getCurrentNetworkView().redrawGraph(True, False)
        myExporter = BitmapExporter('png', 1.0)
        myStream = FileOutputStream(File(imageFileName)) 
        myExporter.export(Cytoscape.getCurrentNetworkView(), myStream)
        myStream.close()
    Cytoscape.destroyNetwork(Cytoscape.getCurrentNetwork() ) 
    ############################################################
    # make a movie
    ############################################################
    ipFile = mypath + 't' + '%d' + '.png'
    opFile = mypath + 'cytoscape_movie.avi'
    cmd = 'ffmpeg -y -r 0.5 -i ' + ipFile + ' ' + opFile
    print cmd
    os.system(cmd)

# CyNetwork.getIdentifier()
# Cytoscape.getEdgeAttributes()
# 
# visualize.py ends here
