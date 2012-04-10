package com.googlecode.kanbanik.model
import org.bson.types.ObjectId
import org.junit.runner.RunWith

class WorkflowitemScalaTest extends BaseIntegrationTest {
  describe("Workflowitem should be able to do all the CRUD operations") {

    it("should be able to find what it stored") {
      val stored = new WorkflowitemScala(None, "name1", 1, None, None, board).store
      val loaded = WorkflowitemScala.byId(stored.id.getOrElse(notSet))
      assert(stored.name === loaded.name)
      assert(stored.wipLimit === loaded.wipLimit)
      assert(stored.id === loaded.id)

    }

    it("should fail when incorrect id specified") {
      intercept[IllegalArgumentException] {
        WorkflowitemScala.byId(new ObjectId("not existing"))
      }
    }

    it("should be able to store subworkflows") {
      val stored = new WorkflowitemScala(None, "name1", 1,
        Some(List(
          new WorkflowitemScala(None, "inner1", 1, None, None, board),
          new WorkflowitemScala(None, "inner2", 1, None, None, board))), None, board).store

      val children = WorkflowitemScala.byId(stored.id.getOrElse(notSet)).children.getOrElse(notSet)
      assert(children.size === 2)
    }

    it("should be able to store more deep subworkflows") {
      val stored = new WorkflowitemScala(None, "name1", 1,
        Some(List(
          new WorkflowitemScala(None, "inner1", 1, None, None, board),
          new WorkflowitemScala(None, "inner2", 1,
            Some(List(
              new WorkflowitemScala(None, "inner21", 1,
                Some(List(
                  new WorkflowitemScala(None, "inner211", 1, None, None, board),
                  new WorkflowitemScala(None, "inner2112", 1, None, None, board))), None, board),
              new WorkflowitemScala(None, "inner22", 1, None, None, board))), None, board))), None, board).store

      val children = WorkflowitemScala.byId(stored.id.getOrElse(notSet)).children.getOrElse(notSet)

      assert(children.size === 2)
      assert(children(0).children.isDefined === false)
      assert(children(1).children.getOrElse(notSet).size === 2)

      assert(children(1).children.getOrElse(notSet)(0).children.getOrElse(notSet).size === 2)
      assert(children(1).children.getOrElse(notSet)(1).children.isDefined === false)
    }

    it("should be able to store also nextItem") {
      val nextItem = Some(WorkflowitemScala.byId(new ObjectId("4f48e10644ae3742baa2d0a9")))
      var stored = new WorkflowitemScala(None, "name1", 1, None, nextItem, board)
      stored = stored.store
      val loaded = WorkflowitemScala.byId(stored.id.getOrElse(notSet))
      val nextItemId = loaded.nextItem.getOrElse(notSet).id.get
      assert(nextItemId === "4f48e10644ae3742baa2d0a9")
    }

    it("should be able to update the primitive parts of the item") {
      val stored = new WorkflowitemScala(Some(new ObjectId("4f48e10644ae3742baa2d0d9")), "other name", 2, None, None, board).store
      val loaded = WorkflowitemScala.byId(new ObjectId("4f48e10644ae3742baa2d0d9"))
      assert(loaded.name === "other name")
      assert(loaded.wipLimit === 2)
    }

    it("should be possible to move the workflowitem from beginning to end") {
      val loaded = WorkflowitemScala.byId(new ObjectId("1f48e10644ae3742baa2d0d9"))
      loaded.nextItem = None
      loaded.store
      assertItemsInOrder(List(
        Some(new ObjectId("2f48e10644ae3742baa2d0d9")),
        Some(new ObjectId("3f48e10644ae3742baa2d0d9")),
        Some(new ObjectId("1f48e10644ae3742baa2d0d9"))))
    }

    it("should be possible to move the workflowitem from end to beginning") {
      val loaded = WorkflowitemScala.byId(new ObjectId("3f48e10644ae3742baa2d0d9"))
      loaded.nextItem = workflowitemOf("1f48e10644ae3742baa2d0d9")
      loaded.store
      assertItemsInOrder(List(
        Some(new ObjectId("3f48e10644ae3742baa2d0d9")),
        Some(new ObjectId("1f48e10644ae3742baa2d0d9")),
        Some(new ObjectId("2f48e10644ae3742baa2d0d9"))))
    }

    it("should be possible to move the workflowitem from middle to beginning") {
      val loaded = WorkflowitemScala.byId(new ObjectId("2f48e10644ae3742baa2d0d9"))
      loaded.nextItem = workflowitemOf("1f48e10644ae3742baa2d0d9")
      loaded.store
      assertItemsInOrder(List(
        Some(new ObjectId("2f48e10644ae3742baa2d0d9")),
        Some(new ObjectId("1f48e10644ae3742baa2d0d9")),
        Some(new ObjectId("3f48e10644ae3742baa2d0d9"))))
    }

    it("should be possible to move the workflowitem from middle to end") {
      val loaded = WorkflowitemScala.byId(new ObjectId("2f48e10644ae3742baa2d0d9"))
      loaded.nextItem = None
      loaded.store
      assertItemsInOrder(List(
        Some(new ObjectId("1f48e10644ae3742baa2d0d9")),
        Some(new ObjectId("3f48e10644ae3742baa2d0d9")),
        Some(new ObjectId("2f48e10644ae3742baa2d0d9"))))
    }

    it("should be possible to stay at the end") {
      val loaded = WorkflowitemScala.byId(new ObjectId("3f48e10644ae3742baa2d0d9"))
      loaded.nextItem = None
      loaded.store
      assertItemsInOrder(List(
        Some(new ObjectId("1f48e10644ae3742baa2d0d9")),
        Some(new ObjectId("2f48e10644ae3742baa2d0d9")),
        Some(new ObjectId("3f48e10644ae3742baa2d0d9"))))
    }

    it("should be possible to stay where you are") {
      val loaded = WorkflowitemScala.byId(new ObjectId("2f48e10644ae3742baa2d0d9"))
      loaded.nextItem = workflowitemOf("3f48e10644ae3742baa2d0d9");
      loaded.store
      assertItemsInOrder(List(
        Some(new ObjectId("1f48e10644ae3742baa2d0d9")),
        Some(new ObjectId("2f48e10644ae3742baa2d0d9")),
        Some(new ObjectId("3f48e10644ae3742baa2d0d9"))))
    }

    it("should be possible to move in more complex boards") {
      val loaded = WorkflowitemScala.byId(new ObjectId("5a48e10644ae3742baa2d0d9"))
      loaded.nextItem = workflowitemOf("2a48e10644ae3742baa2d0d9")
      loaded.store
      assertItemsInOrder(List(
        Some(new ObjectId("1a48e10644ae3742baa2d0d9")),
        Some(new ObjectId("5a48e10644ae3742baa2d0d9")),
        Some(new ObjectId("2a48e10644ae3742baa2d0d9")),
        Some(new ObjectId("3a48e10644ae3742baa2d0d9")),
        Some(new ObjectId("4a48e10644ae3742baa2d0d9")),
        Some(new ObjectId("6a48e10644ae3742baa2d0d9"))))
    }

    it("should be possible to move in two elemnts board from beginning to end") {
      val loaded = WorkflowitemScala.byId(new ObjectId("1b48e10644ae3742baa2d0d9"))
      loaded.nextItem = None
      loaded.store
      assertItemsInOrder(List(
        Some(new ObjectId("2b48e10644ae3742baa2d0d9")),
        Some(new ObjectId("1b48e10644ae3742baa2d0d9"))))
    }

    it("should be possible to move in two elemnts board from end to beginning") {
      val loaded = WorkflowitemScala.byId(new ObjectId("2b48e10644ae3742baa2d0d9"))
      loaded.nextItem = workflowitemOf("1b48e10644ae3742baa2d0d9")
      loaded.store
      assertItemsInOrder(List(
        Some(new ObjectId("2b48e10644ae3742baa2d0d9")),
        Some(new ObjectId("1b48e10644ae3742baa2d0d9"))))
    }

    it("should be possible to delete the first element") {
      val loaded = WorkflowitemScala.byId(new ObjectId("1f48e10644ae3742baa2d0d9"))
      loaded.delete
      assertItemsInOrder(List(
        Some(new ObjectId("2f48e10644ae3742baa2d0d9")),
        Some(new ObjectId("3f48e10644ae3742baa2d0d9"))))
    }

    it("should be possible to delete the last element") {
      val loaded = WorkflowitemScala.byId(new ObjectId("3f48e10644ae3742baa2d0d9"))
      loaded.delete
      assertItemsInOrder(List(
        Some(new ObjectId("1f48e10644ae3742baa2d0d9")),
        Some(new ObjectId("2f48e10644ae3742baa2d0d9"))))
    }

    it("should be possible to delete the middle element") {
      val loaded = WorkflowitemScala.byId(new ObjectId("2f48e10644ae3742baa2d0d9"))
      loaded.delete
      assertItemsInOrder(List(
        Some(new ObjectId("1f48e10644ae3742baa2d0d9")),
        Some(new ObjectId("3f48e10644ae3742baa2d0d9"))))
    }

    it("should be possible to delete all elements") {
      WorkflowitemScala.byId(new ObjectId("1f48e10644ae3742baa2d0d9")).delete
      WorkflowitemScala.byId(new ObjectId("2f48e10644ae3742baa2d0d9")).delete
      WorkflowitemScala.byId(new ObjectId("3f48e10644ae3742baa2d0d9")).delete
    }

    it("should be possible edit primitive of child elements") {
      val loaded = WorkflowitemScala.byId(new ObjectId("1c48e10644ae3742baa2d0d9"))
      loaded.children.get(1).children.get(0).name = "changed name"
      loaded.store
      val toBeChanged = WorkflowitemScala.byId(new ObjectId("1c48e10644ae3742baa2d0d9"))
      assert(loaded.children.get(1).children.get(0).name === "changed name")
    }

    it("should be possible add new children") {
      val loaded = WorkflowitemScala.byId(new ObjectId("1c48e10644ae3742baa2d0d9"))
      val newChildren = new WorkflowitemScala(None, "added", 1, None, None, board) :: loaded.children.get
      loaded.children = Some(newChildren)
      loaded.store
      val toBeChanged = WorkflowitemScala.byId(new ObjectId("1c48e10644ae3742baa2d0d9"))
      assert(loaded.children.get.size === 3)
    }

    it("should remove reference from board to workflow when the workflow is deleted") {
      BoardScala.byId(new ObjectId("4f48e10644ae3742baa2d0d0")).workflowitems.get(0).delete
      assert(BoardScala.byId(new ObjectId("4f48e10644ae3742baa2d0d0")).workflowitems.get.size === 2)
    }

    def workflowitemOf(id: String) = Some(WorkflowitemScala.byId(new ObjectId(id)))

    def board = BoardScala.byId(new ObjectId("1f48e10644ae3742baa2d0b9"))

    def assertItemsInOrder(ids: List[Option[ObjectId]]) {
      ids.foldLeft(ids.head) {
        (actual, expected) =>
          {
            assert(actual === expected)
            val next = WorkflowitemScala.byId(expected.getOrElse(return )).nextItem
            if (!next.isDefined) {
              return ;
            } else {
              next.get.id
            }

          }
      }
    }

    def notSet = {
      throw new IllegalStateException("Required field is not set")
    }
  }
}