package net.sourceforge.ganttproject

import biz.ganttproject.app.Barrier
import biz.ganttproject.app.BarrierEntrance
import net.sourceforge.ganttproject.storage.LazyProjectDatabaseProxy
import net.sourceforge.ganttproject.storage.ProjectEventListenerImpl
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

class ProjectEventListenerImplTest {

  @Test
  fun `projectOpened should shutdown project database`() {
    // Mock the database dependency
    val mockDatabase = mock<LazyProjectDatabaseProxy>()

    // Create listener with mocked trivial dependency
    val listener = ProjectEventListenerImpl(
      projectDatabase = mockDatabase,
      taskManagerSupplier = { mock() },
      calculatedPropertyUpdater = mock(),
      filterUpdater = {}
    )

    // Mock barrier to satisfy method signature
    val mockBarrierRegistry = mock<BarrierEntrance>()
    val mockBarrier = mock<Barrier<IGanttProject>>()

    // Call the method under test
    listener.projectOpened(mockBarrierRegistry, mockBarrier)

    // Verify shutdown was called
    verify(mockDatabase).shutdown()
  }
}