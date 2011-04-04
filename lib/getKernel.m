function varargout = getKernel(Adj,gamma,varargin)
% INPUTS: 
% Adj: directed edge list for the graph. 
% The dimension should be E x 3, E = # of directed edges, 
% column1: index of node1
% column2: index of node2 
% column3: non-negative edge weight. 
% gamma: leakage parameter. 
% large gamma: less diffusion, network reaches equilibium faster
% small gamma: more diffusion. 
% we have used gamma between 0.01 to 8. The performances of different kernels (odd/even/full) have
% different dependence on gamma.
%
% OUTPUTS:
% G1: the even parity kernel, reachability via even length paths 
% G2: the odd parity kernel  
switch nargin
    case 2
        sym = 2;
        fullG = 1;
    case 3
        sym = varargin{1};
        fullG = 1;
    case 4
        sym = varargin{1};
        fullG = varargin{2};
end    
        
% When the number of nodes is large and network is sparse, 
% First map node index in original namespace to a shrunk index space to generate adjacency matix A with nv nodes. 
all_nodes = unique([Adj(:,1);Adj(:,2)]);
nv = length(all_nodes);
N2I(all_nodes) = 1:length(all_nodes);
sprintf('-- max vertex is %d\n', nv);

tic;
% Convert Adj to sparse matrix A and make A symmetric if network is undirected 
% Adj is (i, j, a_ji)
A = sparse(N2I([Adj(:,2);Adj(:,1)]), N2I([Adj(:,1);Adj(:,2)]), [Adj(:,3);Adj(:,3)], nv, nv);
% use A = sparse(N2I(Adj(:,2)), N2I(Adj(:,1)), Adj(:,3), nv, nv) if directed

% change this if your network edge is not binary
A(A > 0) = 1;

% if flag = 1, normalize A matrix so that each column sum to 1
s = diag(sum(A,1));
if ( sym == 1 )
    A = A*inv(s);
% if flag = 2, use symmetric normalization. This method is preferred for genetic and 
% protein interaction networks we analyzed
elseif ( sym == 2 ) 
    A = sqrt(inv(s))*A*sqrt(inv(s));
end

% now construct the self matrix (which is really a vector)
% Sii = sum_j A_ji
S = sum(A);

% the diagonal matrix adds gamma to Self
D = -(S + gamma);
G0 = sparse(1 : nv,1 : nv,-1 ./ D);

maxit = 1000;
tol = 1.e-5;

if ( fullG == 0 )
    [G1,G2,conv] = itermatinv(G0,A,tol,maxit);
    varargout{1} = G1;
    varargout{2} = G2;
    varargout{3} = conv;
else
    [G,conv] = itermatinvG(G0,A,tol,maxit);
    varargout{1} = G;
    varargout{2} = conv;
end

