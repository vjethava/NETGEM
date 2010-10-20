function Q = getQchain (S, seed, mode)
% GETQCHAIN <H1 line>
%
% Usage: Q = getQchain (S, seed, mode)
%
% Returns
% -------
% Q: the markov chain transition matrix
%
% Expects
% -------
% S: the number of states
% seed: RNG seed
% mode: 'random' - generate random

% getQchain.m
% Login : <guest@reva>
% Started on  Mon Oct 12 17:22:46 2009 Vinay Jethava
% $Id$
% 
% Copyright (C) 2009 Vinay Jethava
% This program is free software; you can redistribute it and/or modify
% it under the terms of the GNU General Public License as published by
% the Free Software Foundation; either version 2 of the License, or
% (at your option) any later version.
% 
% This program is distributed in the hope that it will be useful,
% but WITHOUT ANY WARRANTY; without even the implied warranty of
% MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
% GNU General Public License for more details.
% 
% You should have received a copy of the GNU General Public License
% along with this program; if not, write to the Free Software
% Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA
% 02111-1307 USA
%
    if nargin < 3
        mode = 0;
    end
    if nargin >= 2
        rand('state', seed); 
    end
    Q = rand(S, S); 
    % the S=3  (-1 0 1) case.
    if(mode == 1)
        Q(:, 2) = 0.0001*Q(:, 2); 
    end
    
    for i=1:S,
        Q(i, :) = normalize(Q(i, :));
    end
end
