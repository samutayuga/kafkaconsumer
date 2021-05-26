# ${project.name}

${project.name}


## Prerequisites

- Kubernetes 1.9+
- Cassandra 3.11.1+


## Installing the Chart

To install the chart with the release name `my-release`:

```console
$ helm install --name my-release ${project.artifactId}
```

The command deploys ${project.name} on the Kubernetes cluster in the default configuration. The [configuration](#configuration) section lists the parameters that can be configured during installation.

> **Tip**: List all releases using `helm list`

## Uninstalling the Chart

To uninstall/delete the `my-release` deployment:

```console
$ helm delete my-release
```

The command removes all the Kubernetes components associated with the chart and deletes the release.


## Configuration

The following tables lists the configurable parameters of the ${project.name} chart and their default values.

| Parameter                         | Description                            | Default                                                   |
| --------------------------------- | -------------------------------------  | --------------------------------------------------------- |
| `global.docker.registry`          | The docker registry for pulling images | `dockerhub.gemalto.com`                                   |
| `global.docker.imagePullSecrets`  | The list of image pull secrets for k8s | `- name: dockerhub-gemalto`                               |
| `deployment.replicas`             | The replica number for k8s             | `1`                                                       |
| `deployment.affinity`             |                                        | `nil`                                                     |
| `deployment.nodeSelector`         |                                        | `nil`                                                     |
| `deployment.tolerations`          |                                        | `nil`                                                     |
| `deployment.resources`            | CPU/Memory resource requests/limits    | `nil`                                                     |
| `service.type`                    | The kind of k8s service                | `ClusterIP`                                               |
| `logs.level`                      | Logging level                          | `warning`                                                 |

Specify each parameter using the `--set key=value[,key=value]` argument to `helm install`. For example,

```console
$ helm install --name my-release \
  --set global.docker.registry=192.168.0.80 \
    ${project.artifactId}
```

The above command sets the Joomla! administrator account username and password to `admin` and `password` respectively. Additionally it sets the MariaDB `root` user password to `secretpassword`.

Alternatively, a YAML file that specifies the values for the above parameters can be provided while installing the chart. For example,

```console
$ helm install --name my-release -f values.yaml ${project.artifactId}
```

> **Tip**: You can use the default [values.yaml](values.yaml)
