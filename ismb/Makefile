##
## Makefile
## Login : <vjethava@gmail.com>
## Started on  Thu Oct  1 02:02:33 2009 Vinay Jethava
## $Id$
## 
## Copyright (C) 2009 Vinay Jethava
## This program is free software; you can redistribute it and/or modify
## it under the terms of the GNU General Public License as published by
## the Free Software Foundation; either version 2 of the License, or
## (at your option) any later version.
## 
## This program is distributed in the hope that it will be useful,
## but WITHOUT ANY WARRANTY; without even the implied warranty of
## MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
## GNU General Public License for more details.
## 
## You should have received a copy of the GNU General Public License
## along with this program; if not, write to the Free Software
## Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
##

##############################
# Complete this to make it ! #
##############################
NAME 	= 		# Name of executable file
SRC	=		# List of *.c
INCL  	=		# List of *.h
TEX	= $(wildcard *.tex)
TOC	= $(wildcard *.toc)
BBL	= $(wildcard *.bbl)
AUX	= $(wildcard *.aux)
OUT	= $(wildcard *.out)
LOG	= $(wildcard *.log)
BIB	= $(wildcard *.bib)
BLG	= $(wildcard *.blg)
PDF	= $(wildcard *.pdf)
################
# Optional add #
################
IPATH   = -I.           # path of include file
OBJOPT  = -O2 -Wall -Wstrict-prototypes       # option for obj
EXEOPT  = -O2 -Wall -Wstrict-prototypes       # option for exe (-lefence ...)
LPATH   = -L.           # path for librairies ... 

#####################
# Macro Definitions #
#####################
EMACS   = emacs
PDFLATEX= pdflatex
LATEX2HTML= latex2html
HTMLVIEW= firefox
BIBTEX	= bibtex
PDFVIEW = evince
CC 	= gcc
CXX 	= g++
MAKE 	= make
SHELL	= /bin/sh
OBJS 	= $(SRC:.c=.o) 	# WARNING!!! Be careful of your file extensions.
RM 	= /bin/rm -f 	
COMP	= gzip -9v
UNCOMP	= gzip -df
STRIP	= strip

CFLAGS  = $(OBJOPT) $(IPATH)
LDFLAGS = $(EXEOPT) $(LPATH)

.SUFFIXES: .h.Z .c.Z .h.gz .c.gz .c.z .h.z 

##############################
# Basic Compile Instructions #
##############################

all: clean document.pdf clean
$(NAME): $(OBJS) $(SRC) $(INCL)  
	$(CC) $(OBJS) $(LDFLAGS) -o $(NAME) 
#	$(STRIP) ./$(NAME) # if you debug ,don't strip ...

depend:
	gcc $(IPATH) -MM $(SRC) 
clean:
	@$(RM) $(NAME) $(OBJS) *~ 
	$(RM) $(LOG) $(OUT) $(AUX) $(TOC) $(BLG) $(BBL)
fclean:
	-$(RM) $(NAME)
comp: clean
	$(COMP) $(INCL) $(SRC)
ucomp: 
	$(UNCOMP) $(SRC) $(INCL)

.c.Z.c .h.Z.h .c.gz.c .h.gz.h .c.z.c .h.z.h :
	 -$(UNCOMP) $<

$(TEX): $(BIB)

# $(PDFVIEW) $(subst .tex,.pdf,$<)

%.pdf: %.tex 
	$(PDFLATEX) $<
	$(BIBTEX)  $(subst .tex,,$<)
	$(PDFLATEX) $<
	$(PDFLATEX) $<
	clear

.c.o:
	$(CC) $(CFLAGS) -c $< 
.cc.o:
	$(CXX) $(CFLAGS) -c $< 

%.html: %.tex
	$(RM) -rf $(subst .tex,,$<)
	$(LATEX2HTML) $< 

current: current.html current.pdf
	@echo "done making current"
#	$(HTMLVIEW) ./$(subst .html,,$<)/index.html &
#	$(EMACS) --batch --find $< -f org-export-as-html

document: document.html document.pdf
	@echo "done making document"
#	$(HTMLVIEW) ./$(subst .html,,$<)/index.html &
#	$(EMACS) --batch --find $< -f org-export-as-html

notes: notes.org
	$(EMACS) --batch --find $< -f org-export-as-html

strain: strain.pdf
	$(PDFVIEW) $< &x

################
# Dependencies #
################
report: report.pdf 
	$(PDFVIEW) $< &