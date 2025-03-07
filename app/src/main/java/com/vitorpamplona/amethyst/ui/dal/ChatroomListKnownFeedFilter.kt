package com.vitorpamplona.amethyst.ui.dal

import com.vitorpamplona.amethyst.model.Account
import com.vitorpamplona.amethyst.model.Note

object ChatroomListKnownFeedFilter: FeedFilter<Note>() {
  lateinit var account: Account

  // returns the last Note of each user.
  override fun feed(): List<Note> {
    val me = account.userProfile()

    val privateChatrooms = account.userProfile().privateChatrooms
    val messagingWith = privateChatrooms.keys.filter {
      me.hasSentMessagesTo(it) && account.isAcceptable(it)
    }

    val privateMessages = messagingWith.mapNotNull {
      privateChatrooms[it]?.roomMessages?.sortedBy {
        it.event?.createdAt
      }?.lastOrNull {
        it.event != null
      }
    }

    val publicChannels = account.followingChannels().map {
      it.notes.values.filter { account.isAcceptable(it) }.sortedBy { it.event?.createdAt }.lastOrNull { it.event != null }
    }

    return (privateMessages + publicChannels).filterNotNull().sortedBy { it.event?.createdAt }.reversed()
  }

}