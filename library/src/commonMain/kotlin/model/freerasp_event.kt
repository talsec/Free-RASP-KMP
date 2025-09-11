package model

import threat.Threat

sealed class freeraspEvent {
    data class ThreatDetected(val threat: Threat) : freeraspEvent()

    data class MalwareDetected(val apps: List<SuspiciousAppInfo>) : freeraspEvent()
}