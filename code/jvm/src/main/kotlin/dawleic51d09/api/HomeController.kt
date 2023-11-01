package dawleic51d09.api

import dawleic51d09.api.models.HomeOutputModel
import dawleic51d09.api.models.LinkOutputModel
import dawleic51d09.infra.Rels
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.CrossOrigin;

@RestController
@CrossOrigin
class HomeController {

    @GetMapping(Uris.HOME)
    fun getHome() = HomeOutputModel(
        links = listOf(
            LinkOutputModel(
                Uris.home(),
                Rels.SELF
            ),
            LinkOutputModel(
                Uris.home(),
                Rels.HOME
            ),
        ),
        authors = "47181 João Assunção 47531 Guilherme Cepeda"

    )
}