import br.com.telefonica.core.service.TelefonicaReviewVoteService
import de.hybris.platform.core.Registry
import de.hybris.platform.customerreview.enums.CustomerReviewApprovalType
import de.hybris.platform.customerreview.model.CustomerReviewModel
import de.hybris.platform.servicelayer.search.FlexibleSearchService
import de.hybris.platform.servicelayer.user.UserService

def flexibleSearchService = Registry.applicationContext.getBean("flexibleSearchService") as FlexibleSearchService
def userService = Registry.applicationContext.getBean("userService") as UserService
def telefonicaReviewVoteService = Registry.applicationContext.getBean("telefonicaReviewVoteService") as TelefonicaReviewVoteService

def findReview = { String productCode, String userUid ->
    def query = """
        SELECT {r:pk}
        FROM {CustomerReview AS r
        JOIN Product AS p ON {r:product} = {p:pk}
        JOIN Customer AS c ON {r:user} = {c:pk}}
        WHERE {p:code} = ?productCode
        AND {c:uid} = ?userUid
        AND {r:approvalStatus} = ?status
    """
    def params = [
            productCode: productCode,
            userUid    : userUid,
            status     : CustomerReviewApprovalType.APPROVED
    ]
    def result = flexibleSearchService.search(query, params)
    return result.result ? result.result.get(0) as CustomerReviewModel : null
}

def vote = { String productCode, String reviewOwnerUid, String voterUid ->
    try {
        def review = findReview(productCode, reviewOwnerUid)
        def voter = userService.getUserForUID(voterUid)

        if (review == null) {
            println "Review not found — product: ${productCode} owner: ${reviewOwnerUid}"
            return
        }

        if (telefonicaReviewVoteService.hasVoted(review, voter)) {
            println "Already voted — review: ${review.pk} voter: ${voterUid}"
            return
        }

        telefonicaReviewVoteService.vote(review, voter)
        println "Vote registered — product: ${productCode} owner: ${reviewOwnerUid} voter: ${voterUid}"
    }
    catch (Exception e) {
        println "Error voting — product: ${productCode} voter: ${voterUid} error: ${e.message}"
    }
}
println "=== Processing iPhone_13_blue_256g ==="

vote("iPhone_13_blue_256g", "kyle.troop@hybris.com", "john.lucas@hybris.com")
vote("iPhone_13_blue_256g", "kyle.troop@hybris.com", "selfserviceuser5@hybris.com")
vote("iPhone_13_blue_256g", "john.lucas@hybris.com", "kyle.troop@hybris.com")
vote("iPhone_13_blue_256g", "selfserviceuser5@hybris.com", "kyle.troop@hybris.com")
vote("iPhone_13_blue_256g", "selfserviceuser5@hybris.com", "john.lucas@hybris.com")

println "=== Processing iPhone_13_Pro_Max_alpinegreen_1024g ==="

vote("iPhone_13_Pro_Max_alpinegreen_1024g", "kyle.troop@hybris.com", "john.lucas@hybris.com")
vote("iPhone_13_Pro_Max_alpinegreen_1024g", "john.lucas@hybris.com", "selfserviceuser5@hybris.com")


println "Finished!!"
