package com.example.data.firebase

import com.example.base.FirestoreEmulatorTestBase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class FirebaseSecurityRepositoryRuleTest : FirestoreEmulatorTestBase() {

  @Test
  fun unauthenticatedUser_failsWrites() = runBlocking {
    val repository = FirebaseSecurityRepository(firestore)
    auth.signOut()

    val profile = UserProfile(
      userId = "unauth_user",
      email = "unauth@example.com"
    )

    try {
      repository.saveUserProfile(profile)
      assertTrue("Expected IllegalStateException for unauthenticated user", false)
    } catch (e: IllegalStateException) {
      assertTrue(e.message?.contains("User must be signed in") == true)
    }
  }

  @Test
  fun authenticatedUser_canSaveAndReadProfile() = runBlocking {
    val aliceUid = signInTestUser("alice_rule@test.com")
    val repository = FirebaseSecurityRepository(firestore)

    val profile = UserProfile(
      userId = aliceUid,
      email = "alice_rule@test.com",
      displayName = "Alice Security Lead",
      currentRoadmapTrack = "mitre_attack_defend",
      completedTtpCount = 3
    )

    val result = repository.saveUserProfile(profile)
    assertTrue("Profile save should succeed: ${result.exceptionOrNull()}", result.isSuccess)

    val fetched = repository.observeUserProfile(aliceUid).first()
    assertNotNull(fetched)
    assertEquals(aliceUid, fetched?.userId)
    assertEquals("Alice Security Lead", fetched?.displayName)
  }

  @Test
  fun authenticatedUser_canAddAndReadBookmarks() = runBlocking {
    val aliceUid = signInTestUser("alice_bm@test.com")
    val repository = FirebaseSecurityRepository(firestore)

    val bookmark = Bookmark(
      id = "cve_test_1",
      userId = aliceUid,
      threatId = "CVE-2024-21413",
      title = "Microsoft Outlook RCE Vulnerability",
      severity = "CRITICAL",
      source = "NVD"
    )

    val addResult = repository.addBookmark(bookmark)
    assertTrue("Bookmark add should succeed", addResult.isSuccess)

    val bookmarks = repository.observeBookmarks(aliceUid).first()
    assertEquals(1, bookmarks.size)
    assertEquals("CVE-2024-21413", bookmarks[0].threatId)
  }

  @Test
  fun authenticatedUser_canAddAndDeleteNotes() = runBlocking {
    val aliceUid = signInTestUser("alice_note@test.com")
    val repository = FirebaseSecurityRepository(firestore)

    val note = SecurityNote(
      id = "note_ttp_1",
      userId = aliceUid,
      title = "T1059 Command and Scripting Interpreter",
      content = "Review PowerShell execution policy and enable Script Block Logging (EID 4104).",
      ttpId = "T1059"
    )

    val addResult = repository.addSecurityNote(note)
    assertTrue("Note add should succeed", addResult.isSuccess)

    val notes = repository.observeSecurityNotes(aliceUid).first()
    assertEquals(1, notes.size)
    assertEquals("T1059", notes[0].ttpId)

    val delResult = repository.deleteSecurityNote("note_ttp_1")
    assertTrue("Note delete should succeed", delResult.isSuccess)

    val notesAfter = repository.observeSecurityNotes(aliceUid).first()
    assertEquals(0, notesAfter.size)
  }
}
