#! /usr/bin/env python
# pp3.py --- 
# 
# Filename: pp3.py
# Description: 
# Author: Vinay Jethava
# Maintainer: 
# Created: Thu Nov 19 11:15:43 2009 (+0100)
# Version: 
# Last-Updated: Wed Feb  3 15:46:25 2010 (+0530)
#           By: Vinay Jethava
#     Update #: 32
# URL: 
# Keywords: 
# Compatibility: 
# 
# 

# Commentary: 
# 
# Takes an p-p interactions graph and converts to sparse matlab format. 
# I/p: unixfile.txt
# O/p: gene_graph.txt gene_names.txt
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
import os
import sys
counter = 0
numE = 0
genes = dict()
# the original p-p interactions file.
f = open('../data/current/network.txt', 'r')
# the list of genes present.
g = open('./gene_names_pp.txt', 'w')
# the interactions graph in edge adjacency format.
h = open('./gene_graph_pp.txt', 'w')
lines = f.read().split('\r')  
for line in lines:
    # print line
    n = []
    if(len(line.strip()) > 0): 
        cedge = line.strip().split('\t')
        # print cedge
        if(cedge[1] == 'pp'):
         #   print '\tadding edge'
            numE = numE + 1    
            for i in range(3):
                if not i==1:
                    if not genes.has_key(cedge[i]):
                        counter = counter + 1
                        genes[ cedge[i] ] = counter
                        g.write("%s %d\n" % (cedge[i], counter)) 
            #   print genes.get(cedge[i])
                    n.append(genes.get(cedge[i]))
            h.write('%d %d\n' % (n[0], n[1]))
print 'numE: %d numG: %d\n' % (numE, counter) 
f.close()
g.close()
h.close()

# 
# pp_graph_clean.py ends here
