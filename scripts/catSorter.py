#! /usr/bin/env python
# catSorter.py --- 
# 
# Filename: catSorter.py 
# Description: 
# Author: Vinay Jethava
# Maintainer: 
# Created: Thu Nov 19 11:15:43 2009 (+0100)
# Version: 
# Last-Updated: Wed Dec 30 13:54:31 2009 (+0530)
#           By: Vinay Jethava
#     Update #: 77
# URL: 
# Keywords: 
# Compatibility: 
# 
# 

# Commentary: 
# 
# Creates list of categories for genes
# I/p: CAT.funcat
# O/p: gene_cat_ids.txt gene_cat_vals.txt
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
import re
level = 3
gcounter = 0
counter = 0
numE = 0
genes = dict()
cats = dict()
# the original p-p interactions file.
f = open('../data/CAT.funcat', 'r')
# the list of genes present.
g = open('./gc_raw.txt', 'w')
# the interactions graph in edge adjacency format.
h1 = open('./gc_gid.txt', 'w')
h2 = open('./gc_cid.txt', 'w')
h3 = open('./gc_adj.txt', 'w')
lines = f.read().split('\n')
p2 = re.compile('^>(\S*) (\S*) (.*)$')
for line in lines:
    x = p2.match(line)
    if not x==None:
        g.write('%s %s\n' % (x.group(1), x.group(2)) );
        lcat = x.group(2).split(';')
        lgene = x.group(1)
        
        if not genes.has_key(lgene):
            gcounter = gcounter + 1
            genes[lgene] = gcounter 
        for ccat2 in lcat:
            csp = ccat2.split('.')
            ccat = csp[0]
            for i in range(min(level, len(csp)) - 1):
                ccat = ccat + '.' + csp[i+1]
            print lgene, ccat
            if not cats.has_key(ccat):
                counter = counter+1;
                cats[ccat] = counter;
            h3.write('%d %d\n' % (genes[lgene], cats[ccat]))
for (cat, val) in genes.iteritems():
    h1.write('%s %s\n' % (cat, val))
for (cat, val) in cats.iteritems():
    h2.write('%s %s\n' % (cat, val))

        # x2 = p2.match(line)
        # #print x2, line
        # if not x2==None:
        #     print x2.group(1), x2.group(2)
        #     write

f.close()
g.close()
h1.close()
h2.close()
h3.close()
# 
# pp_graph_clean.py ends here
