package model

import threat.Threat

sealed class TalsecEvent {
    data class ThreatDetected(val threat: Threat) : TalsecEvent()

    data class MalwareDetected(val apps: List<SuspiciousAppInfo>) : TalsecEvent()
}