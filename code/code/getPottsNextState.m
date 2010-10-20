function [xo] = getPottsNextState(xi, w, q, n)
% GETPOTTSNEXTSTATE returns the next state by gibbs sampling from the
% current state and the underlying potts model.  
% 
% Usage: [xo] = getNextState(xi, w, q, n)
%
% Expects: 
% -------
% xi: current state
% w: the weights on the edges
% q: the set of states x can take ( default = [-1 1])
% n: number of times to iterate
% 
% Returns:
% -------
% xo: the next state 
%

%%% getPottsNextState.m --- 
%% 
%% Filename: getPottsNextState.m
%% Description: 
%% Author: Vinay Jethava
%% Maintainer: 
%% Created: Mon Nov 23 06:53:47 2009 (+0100)
%% Version: 
%% Last-Updated: Mon Nov 23 07:10:16 2009 (+0100)
%%           By: Vinay Jethava
%%     Update #: 13
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
    if nargin < 4
        n = 1;
    end
    if nargin < 3
        q = [-1 1];
    end
    if nargin < 2
        error('You need to give at least two arguments');
        return,
    end
    if(size(xi, 1) == 1) 
        xi = xi'; % need a row vector 
    end
    xo = xi;
    NX = length(xi); 
    for i=1:n % repeat n times
        for j=1:length(xo) % sample at each xo(j)
            s = w(j, :)*xo;
            P = normalize(exp(-s*q));
            xj_next = q(sample_discrete(P));
            xo = [xo(1:j-1) ; xj_next; xo(j+1:NX)];
        end
    end
end
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%%% getPottsNextState.m ends here
