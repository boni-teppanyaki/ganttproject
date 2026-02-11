package biz.ganttproject.task;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import biz.ganttproject.core.time.GanttCalendar;
import biz.ganttproject.ganttview.NewTaskActor;
import biz.ganttproject.ganttview.TaskTableActionConnector;
import biz.ganttproject.core.time.CalendarFactory;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import net.sourceforge.ganttproject.IGanttProject;
import net.sourceforge.ganttproject.gui.UIFacade;
import net.sourceforge.ganttproject.gui.view.GPViewManager;
import net.sourceforge.ganttproject.resource.HumanResourceManager;
import net.sourceforge.ganttproject.storage.ProjectDatabase;
import net.sourceforge.ganttproject.task.*;
import net.sourceforge.ganttproject.task.hierarchy.TaskHierarchyItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.*;

class TaskActionsFSMTest {

  private IGanttProject projectMock;
  private UIFacade uiFacadeMock;
  private TaskSelectionManager selectionManagerMock;
  private GPViewManager viewManagerMock;
  private TaskTableActionConnector tableConnectorMock;
  private ProjectDatabase databaseMock;

  private TaskManager taskManager;
  private TaskContainmentHierarchyFacade hierarchy;

  private TaskActions taskActions;
  private NewTaskActor<Task> newTaskActorDummy;

  private Task task1;
  private Task task2;

  @BeforeEach
  void setUp() {
    projectMock = mock(IGanttProject.class);
    uiFacadeMock = mock(UIFacade.class);
    selectionManagerMock = mock(TaskSelectionManager.class);
    viewManagerMock = mock(GPViewManager.class);
    tableConnectorMock = mock(TaskTableActionConnector.class);
    databaseMock = mock(ProjectDatabase.class);

    // Mock TaskManager and hierarchy
    taskManager = mock(TaskManager.class);
    hierarchy = mock(TaskContainmentHierarchyFacade.class);
    when(taskManager.getTaskHierarchy()).thenReturn(hierarchy);
    when(projectMock.getTaskManager()).thenReturn(taskManager);
    when(projectMock.getTaskCustomColumnManager()).thenReturn(null);

    // Mock NewTaskActor
    newTaskActorDummy = mock(NewTaskActor.class);

    // Mock the sorted property
    ReadOnlyBooleanWrapper sortedProperty = new ReadOnlyBooleanWrapper(false);
    when(tableConnectorMock.isSorted()).thenReturn(sortedProperty.getReadOnlyProperty());

    // Instantiate TaskActions
    taskActions = new TaskActions(
      projectMock,
      uiFacadeMock,
      selectionManagerMock,
      () -> viewManagerMock,
      () -> tableConnectorMock,
      newTaskActorDummy,
      databaseMock
    );

    // Tasks
    task1 = mock(Task.class);
    task2 = mock(Task.class);
  }


  /*** FSM STATES ***/
  @Test
  void stateNoSelection_allActionsDisabled() {
    assertFalse(taskActions.getIndentAction().isEnabled());
    assertFalse(taskActions.getUnindentAction().isEnabled());
    assertFalse(taskActions.getMoveUpAction().isEnabled());
    assertFalse(taskActions.getMoveDownAction().isEnabled());
  }

  @Test
  void stateSelectionInSortedView_allActionsDisabled() {
    ((ReadOnlyBooleanWrapper) tableConnectorMock.isSorted()).set(true);
    assertFalse(taskActions.getIndentAction().isEnabled());
    assertFalse(taskActions.getUnindentAction().isEnabled());
    assertFalse(taskActions.getMoveUpAction().isEnabled());
    assertFalse(taskActions.getMoveDownAction().isEnabled());
  }

  @Test
  void stateSelectionNotMovable_actionsDisabledDueToHierarchy() {
    // First root task cannot move up or unindent
    assertFalse(taskActions.getMoveUpAction().isEnabled());
    assertFalse(taskActions.getUnindentAction().isEnabled());
  }

  @Test
  void stateSelectionMovable_actionsEnabled() {
    // Second task can move and indent/unindent
    assertTrue(taskActions.getMoveDownAction().isEnabled());
    assertTrue(taskActions.getMoveUpAction().isEnabled());
    assertTrue(taskActions.getIndentAction().isEnabled());
    assertTrue(taskActions.getUnindentAction().isEnabled());
  }

  /*** FSM TRANSITIONS ***/
  @Test
  void transitionSelectTasksFromNoSelection() {
    assertTrue(taskActions.getIndentAction().isEnabled());
  }

  @Test
  void transitionSortTable_disablesActions() {
    ((ReadOnlyBooleanWrapper) tableConnectorMock.isSorted()).set(true);
    assertFalse(taskActions.getIndentAction().isEnabled());
    assertFalse(taskActions.getUnindentAction().isEnabled());
    assertFalse(taskActions.getMoveUpAction().isEnabled());
    assertFalse(taskActions.getMoveDownAction().isEnabled());
  }

  @Test
  void transitionUnsortTable_disablesActions() {
    ((ReadOnlyBooleanWrapper) tableConnectorMock.isSorted()).set(false);
    assertFalse(taskActions.getIndentAction().isEnabled());
    assertFalse(taskActions.getUnindentAction().isEnabled());
    assertFalse(taskActions.getMoveUpAction().isEnabled());
    assertFalse(taskActions.getMoveDownAction().isEnabled());
  }

  @Test
  void transitionClearSelection_returnsToNoSelection() {
    assertFalse(taskActions.getIndentAction().isEnabled());
    assertFalse(taskActions.getUnindentAction().isEnabled());
    assertFalse(taskActions.getMoveUpAction().isEnabled());
    assertFalse(taskActions.getMoveDownAction().isEnabled());
  }
}