function [W_strain] = getStrainWeights(W_orig, K)
% GETSTRAINWEIGHTS returns the modified weights corresponding to
% the diffusion factor given
% 
% Usage: [W_strain] = getStrainWeights(W_orig, K)
% 
% Expects:
% -------
% W_orig - the original weights
% K - the diffusion factor. 
% 

%%% getStrainWeights.m --- 
%% 
%% Filename: getStrainWeights.m
%% Description: 
%% Author: Vinay Jethava
%% Maintainer: 
%% Created: Mon Nov 23 07:11:47 2009 (+0100)
%% Version: 
%% Last-Updated: Mon Nov 23 07:40:00 2009 (+0100)
%%           By: Vinay Jethava
%%     Update #: 6
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
D = diag(1-K); 
assert(size(D) == size(W_orig)); 
W_strain = D * W_orig * D; 
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%%% getStrainWeights.m ends here
