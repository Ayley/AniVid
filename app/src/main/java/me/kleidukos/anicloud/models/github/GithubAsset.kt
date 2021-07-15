package me.kleidukos.anicloud.models.github

import kotlinx.serialization.Serializable

@Serializable
data class GithubAsset(
    val browser_download_url: String?,
    val content_type: String?,
    val created_at: String?,
    val download_count: Int?,
    val id: Int?,
    val label: String?,
    val name: String?,
    val node_id: String?,
    val size: Int?,
    val state: String?,
    val updated_at: String?,
    val uploader: GithubUploader?,
    val url: String?
)