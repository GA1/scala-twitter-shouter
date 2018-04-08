import unittest
import requests


class TestServer(unittest.TestCase):
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
        get_tweets_response = requests.get(self.host + 'api/v1/tweets')
        self.assertEqual(200, get_tweets_response.status_code)


if __name__ == "__main__":
    unittest.main()
