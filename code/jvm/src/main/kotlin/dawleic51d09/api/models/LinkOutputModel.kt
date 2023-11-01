package dawleic51d09.api.models

import dawleic51d09.infra.LinkRelation
import dawleic51d09.infra.Rels
import java.net.URI

data class LinkOutputModel(
    private val targetUri: URI,
    private val relation: LinkRelation
) {
    val href = targetUri.toASCIIString()
    val rel = relation
}