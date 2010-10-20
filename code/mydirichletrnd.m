function [x] = dirichletrnd(A)
% DIRICHLETRND generates a random vectors from a dirichlet
% distribution with parameter A. Uses the gamma RNG from the
% statistics toolbox. 
%
% Usage: [x] = dirichletrnd(A)
%
%
%%% dirichletrnd.m --- 
%% 
%% Filename: dirichletrnd.m
%% Description: 
%% Author: Vinay Jethava
%% Maintainer: 
%% Created: Mon Nov 30 14:16:02 2009 (+0100)
%% Version: 
%% Last-Updated: Mon Nov 30 14:27:29 2009 (+0100)
%%           By: Vinay Jethava
%%     Update #: 4
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
    y = gamrnd(A, 1);
    sy = sum(y); 
    x = y/sy; 
end
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%%% dirichletrnd.m ends here
