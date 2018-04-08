import unittest
import requests


class TestApi(unittest.TestCase):
    host = "http://localhost:8080/"

    def setUp(self):
        pass

    def tearDown(self):
        pass

    def test_health_check(self):
        get_health_check = requests.get(self.host + 'api/health-check')
        self.assertEqual(200, get_health_check.status_code)
        self.assertDictEqual({"message": "Up and healthy."}, get_health_check.json())

    def test_tweets_endpoint_returns_200(self):
        get_tweets_response = requests.get(self.host + 'api/v1/shouted?userName=trump&numberOfTweets=2')
        self.assertEqual(200, get_tweets_response.status_code)

    def test_tweets_endpoint_return_400_if_no_userName_is_provided(self):
        get_tweets_response = requests.get(self.host + 'api/v1/shouted?numberOfTweets=2')
        self.assertEqual(400, get_tweets_response.status_code)

    def test_tweets_endpoint_return_200_even_if_no_numberOfTweets_is_provided(self):
        get_tweets_response = requests.get(self.host + 'api/v1/shouted?userName=trump')
        self.assertEqual(200, get_tweets_response.status_code)


if __name__ == "__main__":
    unittest.main()
