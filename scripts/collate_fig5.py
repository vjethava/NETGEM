# collate_fig5.py --- 
# 
# Filename: collate_fig5.py
# Description: 
# Author: Vinay Jethava
# Maintainer: 
# Created: Thu Jan  7 23:00:06 2010 (+0530)
# Version: 
# Last-Updated: Thu Jan  7 23:22:00 2010 (+0530)
#           By: Vinay Jethava
#     Update #: 13
# URL: 
# Keywords: 
# Compatibility: 
# 
# 

# Commentary: 
# 
# Collate results from AE1 to AE8 to get big graph. 
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
f = open('AEJOINT/graph.sif', 'w') 
for n in range(8):
    s = n+1
    gname = '../results/AE' + str(s) + '/graph.sif'
    g = open(gname)
    gedges = g.read()
    f.write(gedges)
    g.close()
f.close()
for n in range(8):
    t = n + 1
    fname = './AEJOINT/edgeColor/' + 't' + str(t) + '.eda'
    f = open(fname, 'w')
    f.write('edge.color\n')
    for c in range(8):
        gname = '../results/AE' + str(c+1) + '/edgeColor/' + 't' + str(t) + '.eda' 
        g  = open(gname)
        glines = g.read().split('\n')
        for i in range(len(glines) - 1):
            if(len(glines[i+1]) > 0):
                f.write('%s\n' % (glines[i+1])) 
        g.close()
    f.close()

# 
# collate_fig5.py ends here
