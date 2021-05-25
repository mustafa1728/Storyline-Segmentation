build: 
	$(info Compiling code...)
	@javac StoryLineSegmentor.java

average: 
	$(info Processing graph to get average co-occurrences...)
	@java StoryLineSegmentor data/nodes.csv data/edges.csv average

rank: 
	$(info Processing graph to get co-occurrence rankings...)
	@java StoryLineSegmentor data/nodes.csv data/edges.csv rank

segment: 
	$(info Processing graph to get independent storylines...)
	@java StoryLineSegmentor data/nodes.csv data/edges.csv independent_storylines_dfs

all: 
	@java StoryLineSegmentor data/nodes.csv data/edges.csv average && java StoryLineSegmentor.java data/nodes.csv data/edges.csv rank && java StoryLineSegmentor.java data/nodes.csv data/edges.csv independent_storylines_dfs

clean:
	$(info Cleaning up...)
	@rm *.class

