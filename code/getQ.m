function [Q] = getQ(Pi)
% getQ - returns a infinitesimal generator with given steady state
% characteristics for a continuous time markov process
%
% Properties
% -------
%  Pi * Q = 0
%  sum(Q, 2) = 0 
%
% Returns
% -----
% Q - inifintesimal generator
%
% Accepts
% -----
% Pi - the steady state probabilities
%

    
%
% getQ.m
% Login : <vjethava@gmail.com>
% Started on  Sat Oct 10 20:06:17 2009 Vinay Jethava
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
    fprintf(2, 'getQ(): '); 
    N = length(Pi); 
    Aeq = [];
    beq = zeros(2*N, 1); 
    for i=1:N,
        r = []; 
        for j=1:(i-1),
            r = [r zeros(1, N)]; 
        end
        r = [r ones(1, N)];
        for j=(i+1):N,
            r = [r zeros(1, N)];
        end
        Aeq = [Aeq; r]; 
    end
    Aeq = [Aeq; zeros(N, N*N)]; 
    for i=0:(N-1),
        for j=0:(N-1)
            Aeq(i+N+1, N*j +i +1) = Pi(j+1); 
        end
    end
    f = zeros(N*N, 1); 
    A = [];
    b = []; 
    lb = reshape( diag(-inf*ones(N, 1)), N*N, 1); 
    ub = []; 
    q = linprog(f, A, b, Aeq, beq, lb, ub);
    Q = reshape(q, N, N)';
    %% Example for N=3 case
    % Aeq = [ 1  1 1 0 0 0 0 0 0;
    %         0 0 0 1 1 1 0 0 0;
    %         0 0 0 0 0 0 1 1 1; 
    %         Pi(1) 0 0 Pi(2) 0 0 Pi(3) 0 0;
    %         0 Pi(1) 0 0 Pi(2) 0 0 Pi(3) 0;
    %         0 0 Pi(1) 0 0 Pi(2) 0 0 Pi(3);
    %       ];
    % beq = zeros(6, 1); 
    % f = zeros(9, 1); 
    % A = []; 
    % b = []; 
    % lb = [-inf 0 0 0 -inf 0 0 0 -inf]';
    % ub = [0 inf inf inf 0 inf inf inf 0]; 
    % q = linprog(f, A, b, Aeq, beq, lb, ub); 
    % Q = reshape(q, 3, 3)' ;
end
