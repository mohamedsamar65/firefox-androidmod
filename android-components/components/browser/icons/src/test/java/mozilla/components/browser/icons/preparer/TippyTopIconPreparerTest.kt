/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package mozilla.components.browser.icons.preparer

import android.content.res.AssetManager
import androidx.test.ext.junit.runners.AndroidJUnit4
import mozilla.components.browser.icons.IconRequest
import mozilla.components.support.test.any
import mozilla.components.support.test.mock
import mozilla.components.support.test.robolectric.testContext
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.doReturn

@RunWith(AndroidJUnit4::class)
class TippyTopIconPreparerTest {
    @Test
    fun `WHEN url is not in list THEN no resource is added`() {
        val preparer = TippyTopIconPreparer(testContext.assets)

        val request = IconRequest("https://thispageisnotpartofthetippytopylist.org")
        assertEquals(0, request.resources.size)

        val preparedRequest = preparer.prepare(testContext, request)
        assertEquals(0, preparedRequest.resources.size)
    }

    @Test
    fun `WHEN url is not http(s) THEN no resource is added`() {
        val preparer = TippyTopIconPreparer(testContext.assets)

        val request = IconRequest("about://www.github.com")
        assertEquals(0, request.resources.size)

        val preparedRequest = preparer.prepare(testContext, request)
        assertEquals(0, preparedRequest.resources.size)
    }

    @Test
    fun `WHEN list could not be read THEN no resource is added`() {
        val assetManager: AssetManager = mock()
        doReturn("{".toByteArray().inputStream()).`when`(assetManager).open(any())

        val preparer = TippyTopIconPreparer(assetManager)

        val request = IconRequest("https://www.github.com")
        assertEquals(0, request.resources.size)

        val preparedRequest = preparer.prepare(testContext, request)
        assertEquals(0, preparedRequest.resources.size)
    }

    @Test
    fun `WHEN url is Wikipedia THEN prefix is ignored`() {
        val preparer = TippyTopIconPreparer(testContext.assets)

        var request = IconRequest("https://www.wikipedia.org")
        assertEquals(0, request.resources.size)

        var preparedRequest = preparer.prepare(testContext, request)
        assertEquals(1, preparedRequest.resources.size)

        var resource = preparedRequest.resources[0]

        assertEquals("https://www.wikipedia.org/static/apple-touch/wikipedia.png", resource.url)
        assertEquals(IconRequest.Resource.Type.TIPPY_TOP, resource.type)

        request = IconRequest("https://en.wikipedia.org")
        assertEquals(0, request.resources.size)

        preparedRequest = preparer.prepare(testContext, request)
        assertEquals(1, preparedRequest.resources.size)

        resource = preparedRequest.resources[0]

        assertEquals("https://www.wikipedia.org/static/apple-touch/wikipedia.png", resource.url)
        assertEquals(IconRequest.Resource.Type.TIPPY_TOP, resource.type)

        request = IconRequest("https://de.wikipedia.org")
        assertEquals(0, request.resources.size)

        preparedRequest = preparer.prepare(testContext, request)
        assertEquals(1, preparedRequest.resources.size)

        resource = preparedRequest.resources[0]

        assertEquals("https://www.wikipedia.org/static/apple-touch/wikipedia.png", resource.url)
        assertEquals(IconRequest.Resource.Type.TIPPY_TOP, resource.type)

        request = IconRequest("https://de.m.wikipedia.org")
        assertEquals(0, request.resources.size)

        preparedRequest = preparer.prepare(testContext, request)
        assertEquals(1, preparedRequest.resources.size)

        resource = preparedRequest.resources[0]

        assertEquals("https://www.wikipedia.org/static/apple-touch/wikipedia.png", resource.url)
        assertEquals(IconRequest.Resource.Type.TIPPY_TOP, resource.type)

        request = IconRequest("https://abc.wikipedia.org.com")
        assertEquals(0, request.resources.size)

        preparedRequest = preparer.prepare(testContext, request)
        assertEquals(0, preparedRequest.resources.size)
    }
}
