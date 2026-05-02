package com.mikepenz.aboutlibraries.ui.compose.m3.sheet

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.unit.dp
import com.mikepenz.aboutlibraries.entity.Library
import com.mikepenz.aboutlibraries.ui.compose.style.DefaultLibraryActionLabels
import com.mikepenz.aboutlibraries.ui.compose.style.LibrariesStyle
import com.mikepenz.aboutlibraries.ui.compose.style.LibraryActionLabels
import com.mikepenz.aboutlibraries.ui.compose.variant.DefaultLibraryActionVisibility
import com.mikepenz.aboutlibraries.ui.compose.variant.LibraryActionKind
import com.mikepenz.aboutlibraries.ui.compose.variant.LibraryActionMode
import com.mikepenz.aboutlibraries.ui.compose.variant.LibraryActionVisibility
import com.mikepenz.aboutlibraries.ui.compose.variant.LibrarySheetDetail

/**
 * Material 3 wrapper around the theme-agnostic [LibrarySheetDetail].
 *
 * Provides the Material `ModalBottomSheet` shell, drag handle, and surface colors. All inner
 * layout (title, meta, description, license body, action affordances) lives in core.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryDetailSheet(
    library: Library,
    onDismiss: () -> Unit,
    style: LibrariesStyle,
    modifier: Modifier = Modifier,
    actionMode: LibraryActionMode = LibraryActionMode.Chips,
    actionLabels: LibraryActionLabels = DefaultLibraryActionLabels,
    actionVisibility: LibraryActionVisibility = DefaultLibraryActionVisibility,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    dragHandle: (@Composable () -> Unit)? = { DefaultDragHandle(style) },
    onActionClick: ((Library, LibraryActionKind) -> Boolean)? = null,
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        modifier = modifier,
        sheetState = sheetState,
        shape = style.shapes.sheetShape,
        containerColor = style.colors.sheetSurface.takeOrElse { MaterialTheme.colorScheme.surfaceContainerHigh },
        contentColor = style.colors.rowOnBackground.takeOrElse { MaterialTheme.colorScheme.onSurface },
        scrimColor = style.colors.sheetScrim.takeOrElse { MaterialTheme.colorScheme.scrim.copy(alpha = 0.4f) },
        dragHandle = dragHandle,
    ) {
        LibrarySheetDetail(
            library = library,
            actionMode = actionMode,
            style = style,
            actionLabels = actionLabels,
            actionVisibility = actionVisibility,
            onActionClick = onActionClick,
        )
    }
}

@Composable
private fun DefaultDragHandle(style: LibrariesStyle) {
    Box(
        modifier = Modifier
            .padding(top = 12.dp, bottom = 16.dp)
            .size(width = style.dimensions.dragHandleWidth, height = style.dimensions.dragHandleHeight)
            .clip(RoundedCornerShape(style.dimensions.dragHandleHeight / 2))
            .background(style.colors.sheetDragHandle.takeOrElse { MaterialTheme.colorScheme.outline.copy(alpha = 0.5f) }),
    )
}
