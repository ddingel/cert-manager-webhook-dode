package de.wodar.certmanagerwebhookdode.k8s

// K8s supports different UID not only UUID
// https://pkg.go.dev/k8s.io/apimachinery/pkg/types#UID
typealias UID = String

// Types are based on definitions from
// https://github.com/cert-manager/cert-manager/blob/master/pkg/acme/webhook/apis/acme/v1alpha1/types.go
enum class ChallengeAction(val actionType: String) {
    PRESENT("Present"),
    CLEANUP("CleanUp")
}

enum class ACMEChallengeType(val challengeType: String) {
    DNS("dns-01")
}

data class ChallengeRequestDTO(
    val uid: UID,
    val action: ChallengeAction,
    val type: ACMEChallengeType,
    val dnsName: String,
    val key: String,
    val resourceNamespace: String,
    val resolvedFQDN: String,
    val resolvedZone: String,
    val allowAmbientCredentials: Boolean
    // The request can also hold an optional config field
)
data class ChallengeResponseDTO(
    val uid: UID,
    val success: Boolean,
    // The response also can hold an optional status field
)