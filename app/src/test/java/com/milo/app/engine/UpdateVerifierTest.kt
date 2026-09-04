package com.milo.app.engine

import com.milo.app.domain.engine.UpdateVerifier
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class UpdateVerifierTest {

    @get:Rule
    val tempFolder = TemporaryFolder()

    private lateinit var verifier: UpdateVerifier

    @Before
    fun setUp() {
        verifier = UpdateVerifier()
    }

    @Test
    fun calculateSha256_computesAccurateHash() {
        val testFile = tempFolder.newFile("test_package.apk")
        testFile.writeText("MILO_ANDROID_INTEGRITY_TEST_PAYLOAD")

        val hash = verifier.calculateSha256(testFile)

        assertNotNull(hash)
        assertEquals(64, hash.length) // SHA-256 is 64 hex chars
    }

    @Test
    fun verifyApk_withMatchingChecksumAndValidVersion_returnsTrue() {
        val testApk = tempFolder.newFile("milo-release.apk")
        testApk.writeText("VALID_SIGNED_APK_CONTENT")
        val correctHash = verifier.calculateSha256(testApk)

        val isValid = verifier.verifyApk(
            apkFile = testApk,
            expectedSha256 = correctHash,
            expectedMinVersionCode = 10,
            targetVersionCode = 14
        )

        assertTrue(isValid)
    }

    @Test
    fun verifyApk_withMismatchedChecksum_returnsFalse() {
        val testApk = tempFolder.newFile("corrupted-milo.apk")
        testApk.writeText("MODIFIED_TAMPERED_APK_CONTENT")

        val isValid = verifier.verifyApk(
            apkFile = testApk,
            expectedSha256 = "0000000000000000000000000000000000000000000000000000000000000000",
            expectedMinVersionCode = 10,
            targetVersionCode = 14
        )

        assertFalse(isValid)
    }

    @Test
    fun verifyApk_withOutdatedVersionCode_returnsFalse() {
        val testApk = tempFolder.newFile("old-milo.apk")
        testApk.writeText("OLD_VERSION_CONTENT")
        val hash = verifier.calculateSha256(testApk)

        val isValid = verifier.verifyApk(
            apkFile = testApk,
            expectedSha256 = hash,
            expectedMinVersionCode = 15,
            targetVersionCode = 14 // Target code lower than minimum required
        )

        assertFalse(isValid)
    }
}
