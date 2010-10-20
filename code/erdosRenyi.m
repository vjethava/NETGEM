function G = erdosRenyi (n, p)
% ERDOSRENYI Generate the erdos-renyi random graph G(n, p) 
%
% Usage: G = erdosRenyi (n, p)
%
% Returns
% -------
% G: the adjacency matrix for the generated graph
%
% Expects
% -------
% n: number of vertices in the graph 
% p: the edge probability
%%
%% erdosRenyi.m
%% Login : <vjethava@gmail.com>
%% Started on  Fri Oct  9 22:12:10 2009 Vinay Jethava
%% $Id$
%% 
%% Copyright (C) 2009 Vinay Jethava
%% This program is free software; you can redistribute it and/or modify
%% it under the terms of the GNU General Public License as published by
%% the Free Software Foundation; either version 2 of the License, or
%% (at your option) any later version.
%% 
%% This program is distributed in the hope that it will be useful,
%% but WITHOUT ANY WARRANTY; without even the implied warranty of
%% MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
%% GNU General Public License for more details.
%% 
%% You should have received a copy of the GNU General Public License
%% along with this program; if not, write to the Free Software
%% Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
%% 
G = rand(n, n) < p; 
G = triu(G, 1);
G = G + G';
G = sparse(G); 