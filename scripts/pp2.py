# pp_graph_clean.py --- 
# 
# Filename: pp_graph_clean.py
# Description: 
# Author: Vinay Jethava
# Maintainer: 
# Created: Thu Nov 19 11:15:43 2009 (+0100)
# Version: 
# Last-Updated: Thu Nov 19 16:41:15 2009 (+0100)
#           By: Vinay Jethava
#     Update #: 9
# URL: 
# Keywords: 
# Compatibility: 
# 
# 

# Commentary: 
# 
# 
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
import os
import sys

genes = []; 
f = open('./ProtProtInteractions.txt', 'r'); 
lines = f.read()
for line in lines:
    print line
#


# 
# pp_graph_clean.py ends here
