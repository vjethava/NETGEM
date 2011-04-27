#!/usr/bin/env python 
# visualize.py --- 
# 
# Filename: visualize.py
# Description:  Script to convert the files to the required cytoscape format and# get the results out. 
# Author: Vinay Jethava
# Maintainer: 
# Created: Wed Jan  6 14:50:27 2010 (+0530)
# Version: 
# Last-Updated: Wed Apr 27 18:57:02 2011 (+0200)
#           By: Vinay Jethava
#     Update #: 169
# URL: 
# Keywords:
#
# Code:

from cytoscape import Cytoscape
from cytoscape.view import CyNetworkView
from cytoscape.util.export import BitmapExporter
from cytoscape.util.export import PDFExporter
from cytoscape.util.export import SVGExporter
from cytoscape.util.export import PSExporter
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
mainpath = '/Users/vjethava/Etc/NETGEM/results/'
# dnames = [ 'AE1', 'AE2', 'AE3', 'AE4', 'AE5', 'AE6', 'AE7', 'AE8', 'AN1', 'AN2', 'AN3', 'AN4', 'AN5', 'AN6', 'AN7', 'AN8']
# T = 8


dnames = ['JOINT'] #, 'REF', 'MUT']
T = 6
for l in range(len(dnames)):
    mypath = mainpath + dnames[l] + '/' 
    graphFile = mypath + 'graph.sif'
    graph = Cytoscape.createNetworkFromFile(graphFile, True)
    possibleLayouts = CyLayouts.getAllLayouts()
    # layout = CyLayouts.getDefaultLayout()
    # layout.doLayout()
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
        imageFileName = mypath + 't' + str(s) + '.pdf' 
        nodeAttrFiles = []
        edgeAttrFiles = [colorFileName]
        Cytoscape.loadAttributes(nodeAttrFiles, edgeAttrFiles)
        Cytoscape.getCurrentNetworkView().redrawGraph(True, False)
        Cytoscape.getCurrentNetworkView().redrawGraph(True, False)
        myExporter = PDFExporter() # 'pdf', 1.0)
        myExporter.setExportTextAsFont(False) 
        myStream = FileOutputStream(File(imageFileName)) 
        myExporter.export(Cytoscape.getCurrentNetworkView(), myStream)
        myStream.close()
        print "Exported ", t
    # Cytoscape.destroyNetwork(Cytoscape.getCurrentNetwork() ) 
    ############################################################
    # make a movie
    ############################################################
    # ipFile = mypath + 't' + '%d' + '.pdf'
    # opFile = mypath + 'cytoscape_movie.avi'
    # cmd = 'ffmpeg -y -r 0.5 -i ' + ipFile + ' ' + opFile
    # print cmd
    # os.system(cmd)

# CyNetwork.getIdentifier()
# Cytoscape.getEdgeAttributes()
# 
# visualize.py ends here
