function [varargout]=getKernelTest(varargin)
%% [varargout]=getKernelTest(varargin)
% 
%  This function tests the diffusion kernel code provided in Bader
%  (2008). 
% 
%
%
% Author: Vinay Jethava
% Created: Mon Apr  4 16:30:10 2011 (+0200)
% Last-Updated: Mon Apr  4 16:50:30 2011 (+0200)
%           By: Vinay Jethava
%     Update #: 6
% 
% 
%% Change Log:
% 
% 
% 
%% Code:
  path(path, '../lib'); % add ../lib to path if not done

  gamma = 0.8; % diffusion parameter 
  
  G = randsrc(10, 10) - 1;  % graph
  G = sign(G + G'); % symmetric graph
  [I, J, S] = find(G); 
  Adj = [I J S]; % input adjacency list 
  
  [G1, G2] = getKernel(Adj, gamma); 
  keyboard; 
end