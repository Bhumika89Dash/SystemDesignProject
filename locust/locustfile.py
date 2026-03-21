from locust import HttpUser, task, between
import random
class EventUser(HttpUser):
    wait_time = between(1, 3)

    @task
    def get_events(self):
        self.client.get("/events")

    @task
    def create_event(self):
        self.client.post("/event", json={
            "userId": random.randint(100,200),
            "eventType": "LOGIN",
            "payload": "User logged in"
        })