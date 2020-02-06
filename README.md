# WalkShed

A trip planner to route you through as many sidewalk sheds as possible.

## What is a sidewalk shed? 

Glad you asked! See [The New York Times](https://www.nytimes.com/2018/06/14/realestate/sidewalk-construction-sheds-daily-count.html).


## Modules

engine - An instance of [OpenTripPlanner](https://github.com/opentripplanner/OpenTripPlanner) designed to weight street vertices based on whether they contain a sidewalk shed or not vs. the shortest distance or other more "typical" optimizations. 
frontend - A JavaScript front-end for the engine that allows a user to request a trip between two points. Uses the Yahoo! geocoder, which in 2020, I believe is dead. YMMV. 
loader - A PHP backend script to scrape the New York City OpenData portal for records of sidewalk sheds and load them into the graph used by OTP. This happens via a REST API--see the source for details.

If anybody wants to resurrect this idea, I'm happy to provide support via E-mail. Reach out! 
