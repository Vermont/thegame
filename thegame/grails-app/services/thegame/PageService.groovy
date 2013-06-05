package thegame

import security.TeamDetails

class PageService {

    def springSecurityService

    private List<String> errorMessages = [
        "But... the future refused to change.",
        "All your base are belong to them!",
        "How did you even get here?",
        "You've met with a terrible fate, haven't you?",
        "That's the second largest rubber chicken I've ever seen!",
        "You really didn't think it would be that easy, did you?",
        "Get back to where you once belonged.",
        "In a hole in the ground lived a hobbit.  Not a nasty, dirty, wet hole, filled with the ends of worms and an oozy smell, nor yet a dry, bare, sandy hole with nothing in it to sit down on or to eat: it was a hobbit-hole, and that means comfort.",
        "The Babylon Project was our last, best hope for peace. It failed.",
        "Game over, man!  GAME OVER!",
        "FLAGRANT ERROR: COMPUTER OVER, VIRUS = VERY YES",
        "+++out of cheese error. reinstall universe and redo from start+++",
        "Of course you know, this means war... of the worlds!",
        "Space, the final frontier.  These are the voyages of.. you guys.  You seem lost.",
        "Travel the galaxy. Meet fascinating life forms...and kill them.",
        "It is very dark. You will probably be eaten by a grue.",
        "Answer unclear, ask again later."
    ]

    StoryPage dummy = new StoryPage(
        pageNumber: 1,
        title: "testPage1",
        intro: "This is only a test. If this were an actual puzzle, you would have been told what to do.",
        timelockedText: [(new BigDecimal(10000)): "Time is on our side.", (new BigDecimal(60000)): "One minute later."]
    )

    DisplayPage readPage(int pageNumber) {

        TeamDetails team = springSecurityService.currentUser
        DisplayPage page = new DisplayPage(
                pageNumber: pageNumber,
                maxPage: team.currentStatus()
        )

        if (pageNumber <= page.maxPage) {
            StoryPage storyPage = dummy //StoryPage.findByPageNumber(pageNumber)
            page.title = storyPage.title
            page.revealedText = [storyPage.intro]
            long start = team.checkpointsCleared.get(pageNumber).time
            long end
            if (pageNumber < page.maxPage) {
                end = team.checkpointsCleared.get(pageNumber+1).time
            } else {
                end = new Date().time
                long next = storyPage.timelockedText.keySet().find{start + it > end} ?: 0
                if (next) {
                    page.nextReveal = new Date(next)
                }
            }
            page.revealedText.addAll(timelockedText(start, end, storyPage))
        } else {
            page.title = 'Access Denied'
            Random rand = new Random()
            page.revealedText = [errorMessages.get(rand.nextInt(errorMessages.size()))]
        }

        return page
    }

    private List<String> timelockedText(long start, long end, StoryPage storyPage) {
        List<String> text = []
        for (entry in storyPage.timelockedText) {
            if (start + entry.key < end) {
                text.add(entry.value)
            }
        }
        return text
    }
}
