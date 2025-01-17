package fr.inria.coming.spoon.features;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import fr.inria.coming.changeminer.analyzer.commitAnalyzer.HunkDifftAnalyzer;
import fr.inria.coming.changeminer.entity.CommitFinalResult;
import fr.inria.coming.changeminer.entity.FinalResult;
import fr.inria.coming.codefeatures.FeatureAnalyzer;
import fr.inria.coming.codefeatures.FeaturesResult;
import fr.inria.coming.core.entities.AnalysisResult;
import fr.inria.coming.core.entities.DiffResult;
import fr.inria.coming.core.entities.HunkDiff;
import fr.inria.coming.core.entities.RevisionResult;
import fr.inria.coming.core.entities.interfaces.Commit;
import fr.inria.coming.main.ComingMain;
import fr.inria.coming.utils.CommandSummary;

/**
 * 
 * @author Matias Martinez
 *
 */
public class FeaturesOnComingMainTest {

	@Test
	public void testFeaturesOnComingEvolutionFromGit1() throws Exception {
		ComingMain main = new ComingMain();

		CommandSummary cs = new CommandSummary();
		cs.append("-location", "repogit4testv0");
		cs.append("-mode", "features");
		FinalResult finalResult = null;

		finalResult = main.run(cs.flat());

		CommitFinalResult commitResult = (CommitFinalResult) finalResult;

		assertTrue(commitResult.getAllResults().values().size() > 0);

		for (Commit iCommit : commitResult.getAllResults().keySet()) {

			RevisionResult resultofCommit = commitResult.getAllResults().get(iCommit);
			// Get the results of this analyzer
			AnalysisResult featureResult = resultofCommit.get(FeatureAnalyzer.class.getSimpleName());

			assertTrue(featureResult instanceof FeaturesResult);
			FeaturesResult fresults = (FeaturesResult) featureResult;
			assertNotNull(fresults);
		}

	}

	@Test
	public void testFeaturesWithHunkOnComingEvolutionFromGit2() throws Exception {
		ComingMain main = new ComingMain();

		CommandSummary cs = new CommandSummary();
		cs.append("-location", "repogit4testv0");
		cs.append("-mode", "features");
		cs.append("-hunkanalysis", "true");
		FinalResult finalResult = null;

		finalResult = main.run(cs.flat());

		CommitFinalResult commitResult = (CommitFinalResult) finalResult;

		assertTrue(commitResult.getAllResults().values().size() > 0);

		for (Commit iCommit : commitResult.getAllResults().keySet()) {

			RevisionResult resultofCommit = commitResult.getAllResults().get(iCommit);
			// Get the results of this analyzer
			AnalysisResult featureResult = resultofCommit.get(FeatureAnalyzer.class.getSimpleName());
			AnalysisResult hunkResult = resultofCommit.get(HunkDifftAnalyzer.class.getSimpleName());

			assert (hunkResult instanceof DiffResult);
			DiffResult<Commit, HunkDiff> hunkresults = (DiffResult<Commit, HunkDiff>) hunkResult;

			assertNotNull(hunkresults);

			List<HunkDiff> hunks = hunkresults.getAll();
			System.out.println(hunks);

			System.out.println(featureResult);

			assertNotNull(hunks.size());

			assertTrue(featureResult instanceof FeaturesResult);
			FeaturesResult fresults = (FeaturesResult) featureResult;
			assertNotNull(fresults);
		}

	}

	/**
	 * We ignore the execution of this test case: it takes hours, it does only
	 * compute the features but it does not assert the behaviour
	 * 
	 * @throws Exception
	 */
	@Test
	@Ignore
	public void testFeaturesOnComingEvolutionFromFolder1() throws Exception {
		ComingMain main = new ComingMain();

		CommandSummary cs = new CommandSummary();
		cs.append("-input", "files");
		cs.append("-location", (new File("src/main/resources/Defects4J_all_pairs")).getAbsolutePath());
		cs.append("-mode", "features");
		cs.append("-output", "./out_features_d4j");
		cs.append("-parameters", "outputperrevision:true");
		FinalResult finalResult = null;

		finalResult = main.run(cs.flat());

		CommitFinalResult commitResult = (CommitFinalResult) finalResult;

		assertTrue(commitResult.getAllResults().values().size() > 0);

		for (Commit iCommit : commitResult.getAllResults().keySet()) {

			RevisionResult resultofCommit = commitResult.getAllResults().get(iCommit);
			// Get the results of this analyzer
			AnalysisResult featureResult = resultofCommit.get(FeatureAnalyzer.class.getSimpleName());

			assertTrue(featureResult instanceof FeaturesResult);
			FeaturesResult fresults = (FeaturesResult) featureResult;
			assertNotNull(fresults);
			assertNotNull(fresults.getFeatures());

		}

	}

}
