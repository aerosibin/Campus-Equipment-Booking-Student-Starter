# Campus Equipment Booking System

A Spring Boot application for managing campus equipment bookings, with automated testing, Docker containerization, Jenkins CI, and Kubernetes deployment using Minikube.

## Project Overview

The Campus Equipment Booking System allows users to book campus equipment while enforcing booking limits and inventory availability. The project demonstrates a DevOps workflow that includes application development, automated testing, containerization, continuous integration, and Kubernetes orchestration.

### Key Features

- Equipment booking and inventory management
- Maximum of **2 units per booking**
- Inventory availability validation
- Rejection of invalid bookings
- Automated testing using Maven
- Docker image creation
- Jenkins CI pipeline
- Kubernetes deployment and scaling using Minikube

## Technology Stack

| Technology | Purpose |
|---|---|
| Java 17 | Application development |
| Spring Boot | Backend framework |
| Maven | Build and dependency management |
| JUnit | Automated testing |
| Docker | Application containerization |
| Jenkins | Continuous integration |
| Kubernetes | Container orchestration |
| Minikube | Local Kubernetes cluster |
| Git and GitHub | Version control |

## Project Structure

```text
Campus-Equipment-Booking-Student-Starter/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── edu/university/equipment/
│   │   │       └── ApiController.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
├── k8s/
│   ├── deployment.yaml
│   └── service.yaml
├── Dockerfile
├── .dockerignore
├── Jenkinsfile
├── pom.xml
└── README.md
```

*Note: The structure above highlights the main project files. Other source files and directories may be present in the repository.*

## Prerequisites

Install the following tools before running the project:

- JDK 17
- Apache Maven
- Git
- Docker Desktop
- Jenkins
- Minikube
- kubectl

Verify the installations:

```powershell
java -version
mvn -version
git --version
docker --version
kubectl version --client
minikube version
```

## 1. Clone the Repository

```powershell
git clone <YOUR_GITHUB_REPOSITORY_URL>
cd Campus-Equipment-Booking-Student-Starter
```

Replace `<YOUR_GITHUB_REPOSITORY_URL>` with your actual repository URL.

## 2. Build and Test the Application

Run the automated tests:

```powershell
mvn clean test
```

Package the application:

```powershell
mvn clean package
```

Run the application locally:

```powershell
java -jar target/campus-equipment-booking-1.0.0.jar --server.port=8081
```

The application should be accessible at:

```text
http://localhost:8081
```

### Test Results

The reported Maven test execution completed successfully:

```text
Tests run: 10
Failures: 0
Errors: 0
Skipped: 0

BUILD SUCCESS
```

These results confirm that the 10 tests in that execution passed.

## 3. Booking Validation

The application includes a change request to limit each booking to a maximum of two units.

### Acceptance Criteria

| Scenario | Expected Result |
|---|---|
| Booking 1 unit | Accepted if inventory is available |
| Booking 2 units | Accepted if inventory is available |
| Booking more than 2 units | Rejected |
| Requested quantity exceeds available inventory | Rejected |
| Booking is rejected | Inventory and booking state remain unchanged |

The maximum booking limit must be enforced alongside the existing inventory availability check.

### API Testing

Use an API client such as Postman or curl to test the booking endpoint with the appropriate HTTP method and request body.

Opening an endpoint directly in a browser sends a GET request by default. A `405 Method Not Allowed` response means the requested HTTP method is not supported by that endpoint; it does not, by itself, indicate that the application has failed.

Check `ApiController.java` to confirm the supported methods, request format, and endpoint paths.

## 4. Docker Containerization

The application is packaged into a Docker image so it can run consistently across environments.

### Build the Docker Image

Ensure that the Dockerfile and `.dockerignore` are in the project root.

Build the image:

```powershell
docker build -t campus-equipment-booking:set-a .
```

### Run the Container

```powershell
docker run -d --name campus-equipment-booking -p 8081:8081 campus-equipment-booking:set-a
```

Check the running container:

```powershell
docker ps
```

View application logs:

```powershell
docker logs campus-equipment-booking
```

Stop and remove the container when finished:

```powershell
docker stop campus-equipment-booking
docker rm campus-equipment-booking
```

### Dockerfile

The following multi-stage Dockerfile builds the application with Maven and runs it using a Java 17 runtime.

```dockerfile
FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre

WORKDIR /app

COPY --from=build /app/target/campus-equipment-booking-1.0.0.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar", "--server.port=8081"]
```

This Dockerfile assumes the packaged JAR is named `campus-equipment-booking-1.0.0.jar`. If the artifact name in `pom.xml` differs, update the `COPY` instruction accordingly.

### .dockerignore

```text
.git
.idea
.vscode
*.log
```

## 5. Jenkins Continuous Integration

Jenkins automates the application build and testing process whenever the configured pipeline is triggered.

### Pipeline Stages

1. **Checkout** — Retrieve the source code from version control.
2. **Build** — Compile the application.
3. **Test** — Run the automated test suite.
4. **Package** — Generate the executable JAR.
5. **Docker Build** — Build the application image.

### Jenkins Configuration

Configure the Jenkins tools and credentials required by your environment.

If the Jenkinsfile uses named tools such as `JDK17` and `Maven3`, configure those exact names in Jenkins under **Manage Jenkins → Tools**.

For a Windows Jenkins agent, use Windows-compatible pipeline commands such as `bat`. Configure the repository and any webhook or polling trigger according to your Jenkins setup.

A successful pipeline should complete the configured stages without errors.

## 6. Kubernetes Deployment with Minikube

Kubernetes manages the application's container deployment, replicas, and service access.

### Start Minikube

Start Docker Desktop first, then run:

```powershell
minikube start --driver=docker
```

Check cluster status:

```powershell
minikube status
kubectl config current-context
kubectl get nodes
```

The API server and kubelet must be running before applying Kubernetes manifests.

### Load the Docker Image

Load the locally built image into Minikube:

```powershell
minikube image load campus-equipment-booking:set-a
```

Ensure the Kubernetes Deployment uses the same image name and tag:

```yaml
image: campus-equipment-booking:set-a
```

For a locally loaded image, configure the Deployment to use:

```yaml
imagePullPolicy: IfNotPresent
```

### Apply the Kubernetes Manifests

```powershell
kubectl apply -f k8s/deployment.yaml
kubectl apply -f k8s/service.yaml
```

Check the deployment, pods, and services:

```powershell
kubectl get deployments
kubectl get pods
kubectl get services
```

Inspect a pod if it fails to start:

```powershell
kubectl describe pod <POD_NAME>
kubectl logs <POD_NAME>
```

Replace `<POD_NAME>` with an actual pod name returned by `kubectl get pods`.

### Scale the Application

Scale the deployment to three replicas:

```powershell
kubectl scale deployment/campus-equipment-booking --replicas=3
```

Verify the result:

```powershell
kubectl get deployments
kubectl get pods
```

The Deployment should maintain three replicas when the cluster has sufficient resources.

### Verify Self-Healing

Delete one of the existing pods:

```powershell
kubectl delete pod <POD_NAME>
```

Watch the pods:

```powershell
kubectl get pods -w
```

Kubernetes should create a replacement pod to maintain the desired replica count.

### Access the Application

If the Service is configured for Minikube access, run:

```powershell
minikube service campus-equipment-booking --url
```

Open the URL returned by Minikube in your browser or use it with an API client.

The command assumes that the Service is named `campus-equipment-booking`. Confirm the actual name with `kubectl get services`.

## 7. Troubleshooting

### Kubernetes API Server Is Unavailable

If `kubectl` reports connection errors or an inability to download the Kubernetes OpenAPI specification, check the cluster first:

```powershell
minikube status
minikube logs --problems
```

If the API server is stopped, restart Minikube:

```powershell
minikube stop
minikube start --driver=docker
```

Verify that the cluster is healthy:

```powershell
minikube status
kubectl get nodes
```

If the problem persists, inspect the Minikube container logs and Docker Desktop's available memory and disk space before considering cluster recreation.

### Docker Image or JAR Not Found

Check the build output:

```powershell
mvn clean package
Get-ChildItem .\target\*.jar
```

Confirm that the JAR name in the Dockerfile matches the generated file and that `.dockerignore` is not excluding required source files.

### Application Is Not Reachable

Check the container and Kubernetes resources:

```powershell
docker ps
docker logs campus-equipment-booking
kubectl get pods
kubectl get services
kubectl describe deployment campus-equipment-booking
```

Confirm that the application listens on port `8081` and that the Kubernetes Service's `targetPort` matches the container port.

## 8. DevOps Workflow

```text
Developer
   |
   v
Git / GitHub
   |
   v
Jenkins Pipeline
   |
   +--> Build
   |
   +--> Automated Tests
   |
   +--> Package JAR
   |
   +--> Build Docker Image
             |
             v
      Minikube Image Load
             |
             v
      Kubernetes Deployment
             |
             v
       Kubernetes Service
             |
             v
        Application Access
```

## 9. Project Deliverables

- [ ] Spring Boot application source code
- [ ] Maven build and automated test execution
- [ ] Docker image build
- [ ] Docker image loaded into Minikube
- [ ] Jenkins pipeline execution verified
- [ ] Kubernetes Deployment and Service successfully applied
- [ ] Three replicas verified
- [ ] Pod replacement verified
- [ ] Application access through the Kubernetes Service verified

Update the checklist as each task is completed and verified. A successfully built Docker image or loaded Minikube image does not, by itself, prove that the Kubernetes deployment is running.

## Conclusion

The Campus Equipment Booking System demonstrates a practical DevOps workflow for a Spring Boot application. It combines application validation, automated testing, Docker containerization, Jenkins-based continuous integration, and Kubernetes deployment using Minikube.

The project also incorporates booking validation rules, including the maximum of two units per booking and inventory availability checks.

The final deployment status should be confirmed using Jenkins build results, Kubernetes resource status, and successful application access.
