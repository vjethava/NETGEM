function xn = getNextGibbsState (xi, w)
% getNextGibbsState - returns the next state by gibbs sampling
%
% Usage: xn = getNextGibbsState (xi, w)
%
% Returns
% -------
% xn: the next state (row-vector)
%
% Expects
% -------
% xi: initial state (row-vector)
% w: weight distribution
%    
    
% getNextGibbsState.m
% Login : <vjethava@gmail.com>
% Started on  Sun Oct 11 19:31:15 2009 Vinay Jethava
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
% Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
%
    
% The updated gibbs sample for state i is according to
% $P(x^i = 1 | X_{\i}) = \frac{ 1 }{1 + \exp \{-\sum_{j \in N(i)}  w_{ij} x_j \} }$
%
    L = 1; % number of times to loop through
    flipped = 0; 
    if(size(xi, 1) > 1)
        xi = xi';
        flipped = 1;
    end
    assert(size(xi, 1) == 1, 'Invalid data: x'); 
    N = length(xi); 
    xs = xi; % the updated state
    for l=1:L,
        for i=1:N,
            assert(nnz(w(i, :) - w(:, i)') == 0);
            s = w(i, :) * xs';
            p = 1.0/(1.0 + exp(2*s));
            u = rand();
            if( u <= p)
                xs = [xs(1:(i-1)) 1 xs((i+1):N)];
            else
                xs = [xs(1:(i-1)) -1 xs((i+1):N)];
            end
        end
        [E, P] = getStateEnergy(xs, w); 
        m = sprintf(['gibbsNextState() energy of state: %g probability of ' ...
                     'state: %g'], E, P);
        %  disp(m); 
        %  disp(xs); 
        %        keyboard;
    end
    if(flipped > 0)
        xn = xs';
    else
        xn = xs;
    end
end






