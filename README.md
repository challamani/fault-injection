# fault-injection
This is `fault-injection` service designed to test a system resilience, fault tolerance, error handling, and fail-over scenarios.

## Table of Contents
- [About Fault-injection](#about-fault-injection)
- [What are the benefits](#Benefits)
- [Usage](#usage)

### About Fault-injection

This service is born from the fault injection capabilities of [Istio](https://istio.io/latest/docs/tasks/traffic-management/fault-injection/)
Istio, as a service mesh solution for Kubernetes, offers fault injection capabilities as part of its traffic management features. Fault injection involves intentionally introducing failures into your system to test its resilience and fault tolerance, but Istio's fault-injection resources we can apply to mesh enabled Kubernetes workloads.

This service provides fault injection functionality for individual microservices, whether they're deployed in Kubernetes clusters (non mesh workloads) or standalone instances on VMs.

### Benefits
Here are some benefits of Istio fault injection

- Resilience Testing: 
  - Fault injection allows you to simulate real-world failure scenarios in a controlled environment. By introducing faults such as latency, errors, or service unavailability. 
  - You can verify how your applications and services respond under adverse conditions.
- Failure Mode Analysis: 
  - Fault injection capabilities help you identify weaknesses and vulnerabilities in your system's design and architecture. By inducing failures, you can uncover potential failure modes and assess whether your system can gracefully handle them.
- Dependency Resilience: 
  - In microservices architectures, applications rely on multiple services and dependencies. Fault injection enables you to evaluate how well your services handle failures or degraded performance of their dependencies. 
  - This helps in designing resilient systems with accurate error-handling mechanisms.
- Continuous Validation: 
  - Incorporating fault injection into your CI/CD pipelines enables continuous validation of your system's resilience as code changes are deployed. This ensures that your applications remain resilient to failures even as they evolve over time.
  - In summary, fault injection capabilities offer a powerful toolset for assessing and improving the resilience and fault tolerance of your microservices-based applications running on Kubernetes, Docker Environment or Standalone Java Instance. 
  - By intentionally introducing failures, you can identify weaknesses, optimize performance, and build more reliable systems.

### Usage

This `fault-injection` service can be a lib service that you add use as `maven` dependency in your spring-boot microservice, but currently this repo consists of an example way of implementing `fault-injection` filter that has to be executed after `authentication/authorization` layer in your rest-api implementation.

Note: The `fault-injection` service works on flag `service.fault-injection=true`, by default it is `false` to skip loading the `fault-injection service` as we may not require this type of functionalities across all environments.  

#### Fault-injection config
```json
[
  {
    "type": "delay",
    "fixedDelay": 5000,
    "priority": 1,
    "matches": [
      {
        "methods": [
          "DELETE",
          "POST",
          "GET"
        ],
        "headers": [
          {
            "name": "server",
            "value": "resilience-test"
          }
        ]
      }
    ]
  },
  {
    "type": "abort",
    "httpStatus": 500,
    "priority": 2,
    "matches": [
      {
        "methods": [
          "DELETE",
          "POST",
          "PUT"
        ],
        "headers": [
          {
            "name": "server",
            "value": "resilience-test"
          }
        ]
      }
    ]
  }
]
```
The above specified fault-injection config is pretty much self-explanatory, it contains `abort` and `fixed-delay` fault injection configs with fixed values, that will get executed on your test traffic that are matching with the `fault-injection conditions`.

- An example curl request that will be eligible for `delay` fault-injection.
    ```curl
    curl -X GET http://localhost:9090/fault-injection/employees -H "server: resilience-test"
    ```
- An example curl request that will be eligible for both `abort` and `delay` fault-injections
    ```curl
    curl -X DELETE http://localhost:9090/fault-injection/employees/1 -H "server: resilience-test"  
    ```
- An example curl request that will be eligible for `delay` fault-injections with runtime request specific values `overrideFixedDelay`
  ```curl
    curl -X DELETE http://localhost:9090/fault-injection/employees \
    -H "server: resilience-test" \
    -H "overrideFixedDelay: 2000" 
  ```

#### Future plan
```maven
#Currently this service is not published to maven repo, this is going to be a future plan.
<dependency>
	<groupId>com.challamani.fault-injection</groupId>
	<artifactId>fault-injection</artifactId>
	<optional>true</optional>
</dependency>
```