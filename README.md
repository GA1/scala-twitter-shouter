# scala-twitter-shouter

An applicaitons erver returning the last `n` tweets for a user with `username`. Written with Scala + Akka. Integration tests with python + requests.

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
http://localhost:8080/api/v1/shouted?userName=realDonaldTrump&numberOfTweets=123
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