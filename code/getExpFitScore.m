function [changeScore, freqC, binC] = getExpFitScore(vW, nBins, fractionToSee) 
%%% getExpFitScore.m --- 
%% 
%% Filename: getExpFitScore.m
%% Description: 
%% Author: Vinay Jethava
%% Maintainer: 
%% Created: Wed Jan  6 23:18:21 2010 (+0530)
%% Version: 
%% Last-Updated: Wed Jan  6 23:21:41 2010 (+0530)
%%           By: Vinay Jethava
%%     Update #: 8
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
if nargin < 3
    fractionToSee = 0.05
end

if nargin < 2
    nBins = 6; 
end
totVarianceSum = sum(vW);
[freqC, binC] = hist(vW, nBins); 
[p, s] = polyfit(binC, log(freqC), 1); 
est = exp(p(1)*binC + p(2))';
changeScore = log(fractionToSee)/p(1); 

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%%% getExpFitScore.m ends here
