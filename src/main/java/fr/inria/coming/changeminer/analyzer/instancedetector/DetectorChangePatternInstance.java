package fr.inria.coming.changeminer.analyzer.instancedetector;

import java.util.List;

import com.github.gumtreediff.actions.model.Action;
import com.github.gumtreediff.actions.model.Delete;
import com.github.gumtreediff.actions.model.Insert;
import com.github.gumtreediff.actions.model.Move;
import com.github.gumtreediff.actions.model.Update;

import fr.inria.astor.util.MapList;
import fr.inria.coming.changeminer.analyzer.patternspecification.ChangePatternSpecification;
import fr.inria.coming.changeminer.analyzer.patternspecification.ParentPatternEntity;
import fr.inria.coming.changeminer.analyzer.patternspecification.PatternAction;
import fr.inria.coming.changeminer.analyzer.patternspecification.PatternEntity;
import fr.inria.coming.changeminer.entity.ActionType;
import gumtree.spoon.diff.Diff;
import gumtree.spoon.diff.operations.Operation;
import spoon.reflect.declaration.CtElement;

/**
 * 
 * @author Matias Martinez
 *
 */
public class DetectorChangePatternInstance {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<ChangePatternInstance> findPatternInstances(ChangePatternSpecification changePatternSpecification,
			Diff diffToAnalyze) {

		MapList<PatternAction, Operation<Action>> mapping = s1mappingActions(changePatternSpecification, diffToAnalyze);
		// Now, Parent analysis:

		return null;

	}

	/**
	 * 
	 * @param changePatternSpecification
	 * @param diffToAnalyze
	 * @return
	 */
	public MapList<PatternAction, Operation<Action>> s1mappingActions(
			ChangePatternSpecification changePatternSpecification, Diff diffToAnalyze) {
		List<Operation> operations = diffToAnalyze.getAllOperations();

		MapList<PatternAction, Operation<Action>> mapping = new MapList<>();

		// For each abstract change in the pattern
		for (PatternAction patternAction : changePatternSpecification.getAbstractChanges()) {
			boolean mapped = false;
			// For each operation
			for (Operation operation : operations) {

				Action action = operation.getAction();

				if (matchValues(operation, patternAction.getAffectedEntity().getValue())
						&& matchTypeLabel(operation, getTypeLabel(patternAction))
						&& matchActionTypes(action, getOperationType(patternAction))
						&& matchParentElements(operation, patternAction.getAffectedEntity())) {
					mapped = true;
					mapping.add(patternAction, operation);
				}
			}
			if (!mapped) {
				System.out.println("Abstract change not mapped: " + patternAction);
			}
		}
		return mapping;
	}

	private boolean matchParentElements(Operation affectedOperation, PatternEntity affectedEntity) {

		ParentPatternEntity parentEntityFromPattern = affectedEntity.getParentPatternEntity();

		if (parentEntityFromPattern == null) {
			return true;
		}
		int parentLevel = parentEntityFromPattern.getParentLevel();
		PatternEntity parentEntity = parentEntityFromPattern.getParent();
		// Let's get the parent of the affected
		CtElement parentNodeFromAction = affectedOperation.getNode().getParent();

		int i_levels = 1;
		// Scale the hierarchie and check types.
		while (parentNodeFromAction != null && i_levels <= parentLevel) {
			String typeOfNode = getNodeLabelFromCtElement(parentNodeFromAction);
			String valueOfNode = parentNodeFromAction.toString();

			if ( // type
			"*".equals(parentEntity.getEntityType())
					|| (typeOfNode != null && typeOfNode.equals(parentEntity.getEntityType()))

							&& // value

							"*".equals(parentEntity.getValue())
					|| (valueOfNode != null && valueOfNode.equals(parentEntity.getValue()))

			) {

				i_levels = 1;
				parentLevel = parentEntityFromPattern.getParentLevel();
				parentEntityFromPattern = parentEntity.getParentPatternEntity();

				if (parentEntityFromPattern == null) {
					return true;
				}
				parentEntity = parentEntityFromPattern.getParent();

			} else {
				i_levels++;
			}
			parentNodeFromAction = parentNodeFromAction.getParent();
		}

		return false;

	}

	public static boolean matchValues(Operation operation, String nodeValue) {
		return nodeValue == null || "*".equals(nodeValue) || operation.getNode().toString().equals(nodeValue)
				|| (operation.getDstNode() != null && (operation.getDstNode()).toString().equals(nodeValue))
				|| (operation.getSrcNode() != null && (operation.getSrcNode()).toString().equals(nodeValue));

	}

	public static boolean matchActionTypes(Action action, ActionType type) {

		return ActionType.ANY.equals(type) || (type.equals(ActionType.INS) && (action instanceof Insert))
				|| (type.equals(ActionType.DEL) && (action instanceof Delete))
				|| (type.equals(ActionType.MOV) && (action instanceof Move))
				|| (type.equals(ActionType.UPD) && (action instanceof Update));
	}

	protected boolean matchTypeLabel(Operation operation, String typeLabel) {
		return typeLabel == null || "*".equals(typeLabel)
				|| operation.getNode() != null && getNodeLabelFromCtElement(operation.getNode()).equals(typeLabel)
				|| (operation.getDstNode() != null
						&& getNodeLabelFromCtElement(operation.getDstNode()).equals(typeLabel))
				|| (operation.getSrcNode() != null
						&& getNodeLabelFromCtElement(operation.getSrcNode()).equals(typeLabel));
	}

	public ActionType getOperationType(PatternAction patternAction) {
		return patternAction.getAction();
	}

	public String getTypeLabel(PatternAction patternAction) {
		return patternAction.getAffectedEntity().getEntityType();
	}

	/**
	 * The label of a CtElement is the simple name of the class without the CT
	 * prefix.
	 * 
	 * @param element
	 * @return
	 */
	public String getNodeLabelFromCtElement(CtElement element) {
		String typeFromCt = element.getClass().getSimpleName();
		if (typeFromCt.trim().isEmpty())
			return typeFromCt;
		return typeFromCt.substring(2, typeFromCt.length() - 4);
	}

}
