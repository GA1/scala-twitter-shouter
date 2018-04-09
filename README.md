## feedback
Hello! No matter whether you'll find this code great or unacceptable, I would really appreciate having
your feedback as I have spent around 15 hours for finishing the task. 

# scala-twitter-shouter

### prerequisites:
- sbt
- scala
- python3
- pip3

## how to run the server
```
cd backend
export TWITTER_CONSUMER_KEY={your twitter consumer key}  && 
export TWITTER_CONSUMER_SECRET={your twitter consumer secret} && 
sbt run
```

and then go to:
```
http://localhost:8080/api/health-check
```
or
```
http://localhost:8080/api/v1/tweets
```

## how to run integration & unit tests:
```
cd backend
sbt test
```

##  how to run end2end tests (once the server runs)
```
cd e2e-tests
```
run the following if run for the first time
```
python3 -m venv venv 
source venv/bin/activate 
pip3 install -r requirements.txt
```

then for running the tests execute:
```
python3 test_api.py
```

Also, instead of all above you can run the e2e tests with a script:
```
cd e2e-tests 
e2e-test.sh
```



### explanations:
1. I use scala + akka as asked in the instruction 
2. I use python for end2end testing because it's much more lightweight and faster. It has its disadvantages, namely: the 
  developer needs to have another dependency beside sbt + scala. This problem would be solved once the project is
  dockerized (see below)
3. I wrap each api response with dataerror wrapper in order to give the users more information in case of failures  

### possible improvements:
1. I use a custom dependency injection with modified cake-pattern. Once the project grows it would become unbearable 
to build all the beans by ourselves. An annotation-based dependency injection framework like Guice would be useful.
2. dockerizing - the whole app should be dockerized to make eat much easier for testing and running, a docker image
  would have to contain both python + scala dependencies. I would argue that python's speed for running e2e tests is worth
  having a more heavy-weight docker image. I have not dockerized the project since I was explicitely said that it's not 
  something that can give me any bonus points.
3. I could add many more exceptions depending on the twitter response or just return twitter error codes and messages
  to the user
4. Improving the authentication flow. An external bearer token invalidation may be exucted outside the app (for example, 
  in case of token compromisation). The app should react accordingly - that is reauthenticate itself. 
5. Shouting could be improved, for example, the links inside tweets should not be upper-cased. The instruction does 
  not mention it, hence I did not add this 'feature'
6. Caching, I am sorry for not being able to implementing the cache. I had not enough to do it.