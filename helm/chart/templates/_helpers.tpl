
{{/* vim: set filetype=mustache: */}}

{{/* Generate a fully qualified app name */}}
{{/* We truncate at 63 chars because some Kubernetes name fields are limited to this (by the DNS naming spec)*/}}
{{- define "{{ .Chart.Name }}.fullname" -}}
{{- $releaseName := .Release.Name | replace "_" "-" -}}
{{- printf "%s" .Chart.Name | trunc 63 | trimSuffix "-" -}}
{{- end -}}

{{/* Remove '+' from semVer specification for being compliant with k8s */}}
{{- define "{{ .Chart.Version }}.version" -}}
{{- printf "%s" .Chart.Version | replace "+" "_" | trunc 63 | trimSuffix "-" -}}
{{- end -}}

{{/* Generate basic labels */}}
{{- define "{{ .Chart.Name }}.labels" -}}
labels:
  app.kubernetes.io/name: {{ .Chart.Name }}
  app.kubernetes.io/version: {{ .Chart.AppVersion | replace "+" "_" }}
  app.kubernetes.io/instance: {{ .Release.Name }}
  app.kubernetes.io/managed-by: {{ .Release.Service }}
  helm.sh/chart: {{ .Chart.Name }}-{{ .Chart.Version | replace "+" "_" }}
{{- end -}}

{{/*
Return the http_port where the server is running
*/}}
{{- define "{{ .Chart.Name }}.port" -}}
{{- if hasKey .Values.apps "port" -}}
{{- printf "%g" .Values.apps.port | trunc 63 |trimSuffix "-" -}}
{{- else -}}
{{- printf "80" -}}
{{- end -}}
{{- end -}}