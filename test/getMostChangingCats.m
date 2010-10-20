%%% getMostChangingCats.m --- 
%% 
%% Filename: getMostChangingCats.m
%% Description: 
%% Author: Vinay Jethava
%% Maintainer: 
%% Created: Fri Jan  8 11:44:53 2010 (+0530)
%% Version: 
%% Last-Updated: Fri Jan  8 12:27:47 2010 (+0530)
%%           By: Vinay Jethava
%%     Update #: 35
%% URL: 
%% Keywords: 
%% Compatibility: 
%% 
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%% 
%%% Commentary: 
%% 
%% 
%% 
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%% 
%%% Change log:
%% 
%% 
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%% 
%% This program is free software; you can redistribute it and/or
%% modify it under the terms of the GNU General Public License as
%% published by the Free Software Foundation; either version 3, or
%% (at your option) any later version.
%% 
%% This program is distributed in the hope that it will be useful,
%% but WITHOUT ANY WARRANTY; without even the implied warranty of
%% MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
%% General Public License for more details.
%% 
%% You should have received a copy of the GNU General Public License
%% along with this program; see the file COPYING.  If not, write to
%% the Free Software Foundation, Inc., 51 Franklin Street, Fifth
%% Floor, Boston, MA 02110-1301, USA.
%% 
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%% 
%%% Code:
% function [myClassNames] = getMostChangingCats(hj, N)
N = 20; 
load ../results/result2s_sf2.mat; 
hjSorted = sortH(hj); 
refSorted = sortH(hs(1));
mutSorted = sortH(hs(2)); 
load ../data/categoryLookup.mat; 
myClassIdx = hjSorted.classes(1:N); 
myClassScore = hjSorted.changeProb(1:N); 
myClassNames = {};
fid  = fopen('mc_cats.txt', 'w'); 
for i=1:N
    fprintf(fid, '%s & %s & %s \\\\ \n', char(refSorted.classes(i)), ...
            char(mutSorted.classes(i)), char(hjSorted.classes(i))); 
end
fprintf(fid, '\n'); 

mc_map = containers.Map(); 
count =  0; 
for i=1:N
    mc_map(char(hjSorted.classes(i)) ) = char( my_map(char(hjSorted.classes(i)) ) );
    mc_map(char(refSorted.classes(i)) ) = char( my_map(char(refSorted.classes(i)) ) );
    mc_map(char(mutSorted.classes(i)) ) = char( my_map(char(mutSorted.classes(i)) ) );     
end
v = mc_map.values'; 
for j=1:length(v)
    fprintf(fid, '%s \\\\\n', char(v(j))); 
end
fclose(fid); 
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%%% getMostChangingCats.m ends here
