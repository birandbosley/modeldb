package ai.verta.modeldb.versioning.blob.diff;

import ai.verta.modeldb.versioning.DiffStatusEnum;
import ai.verta.modeldb.versioning.autogenerated._public.modeldb.versioning.model.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// TODO: handle collisions instead of just overwriting? It will be useful for merging
public class DiffMerger {
  public static <T, T2> T2 getOrNull(T v, Function<T, T2> getter) {
    if (v == null) {
      return null;
    }
    return getter.apply(v);
  }

  public static <B, D, F, DF> F merge(
      B a, D d, Function<B, F> getterA, Function<D, DF> getterD, Function3<F, DF, F> merger) {
    if (d == null) {
      return getOrNull(a, getterA);
    }
    return merger.apply(getOrNull(a, getterA), getOrNull(d, getterD));
  }

  public static <B, D, F, DF> List<F> mergeList(
      B a,
      D d,
      Function<B, List<F>> getterA,
      Function<D, List<DF>> getterD,
      Function<F, String> hasherA,
      Function<DF, String> hasherD,
      Function3<F, DF, F> merger) {
    HashMap<String, HashSet<F>> mapA = new HashMap<>();
    HashMap<String, HashSet<DF>> mapD = new HashMap<>();
    getterA.apply(a).forEach(el -> mapA.getOrDefault(hasherA.apply(el), new HashSet<>()).add(el));
    getterD.apply(d).forEach(el -> mapD.getOrDefault(hasherD.apply(el), new HashSet<>()).add(el));

    HashSet<String> keys = new HashSet<>();
    keys.addAll(mapA.keySet());
    keys.addAll(mapD.keySet());

    return keys.stream()
        .flatMap(
            key -> {
              HashSet<F> elA = mapA.get(key);
              HashSet<DF> elD = mapD.get(key);
              // Key collision and one element, process it
              if (elA != null && elD != null && elA.size() == 1 && elD.size() == 1) {
                return Stream.of(merger.apply(elA.iterator().next(), elD.iterator().next()));
              }

              // Key collision and more elements, consider removal + addition
              if (elA != null && elD != null) {
                return Stream.concat(
                    elA.stream().map(el -> merger.apply(el, null)),
                    elD.stream().map(el -> merger.apply(null, el)));
              } else if (elA != null) {
                return elA.stream().map(el -> merger.apply(el, null));
              } else {
                return elD.stream().map(el -> merger.apply(null, el));
              }
            })
        .filter(
            x -> x != null) // Remove elements that became null in the process of applying the diff
        // for some reason
        .collect(Collectors.toList());
  }

  public static <T, T2> T mergeLast(
      T a, T2 d, Function<T2, T> getB, Function<T2, DiffStatusEnumDiffStatus> getStatus) {
    if (d == null) {
      return a;
    }

    DiffStatusEnumDiffStatus status = getStatus.apply(d);
    if (status.Status == DiffStatusEnum.DiffStatus.ADDED
        || status.Status == DiffStatusEnum.DiffStatus.MODIFIED) {
      return getB.apply(d);
    }
    if (status.Status == DiffStatusEnum.DiffStatus.DELETED) {
      return null;
    }
    return a;
  }

  public static Blob mergeBlob(Blob a, BlobDiff d) {
    return new Blob()
        .setCode(merge(a, d, x -> x.Code, x -> x.Code, DiffMerger::mergeCode))
        .setConfig(merge(a, d, x -> x.Config, x -> x.Config, DiffMerger::mergeConfig))
        .setDataset(merge(a, d, x -> x.Dataset, x -> x.Dataset, DiffMerger::mergeDataset))
        .setEnvironment(
            merge(a, d, x -> x.Environment, x -> x.Environment, DiffMerger::mergeEnvironment));
  }

  public static CodeBlob mergeCode(CodeBlob a, CodeDiff d) {
    return new CodeBlob()
        .setGit(merge(a, d, x -> x.Git, x -> x.Git, DiffMerger::mergeGitCode))
        .setNotebook(merge(a, d, x -> x.Notebook, x -> x.Notebook, DiffMerger::mergeNotebookCode));
  }

  public static GitCodeBlob mergeGitCode(GitCodeBlob a, GitCodeDiff d) {
    return mergeLast(a, d, x -> d.B, x -> x.Status);
  }

  public static NotebookCodeBlob mergeNotebookCode(NotebookCodeBlob a, NotebookCodeDiff d) {
    return new NotebookCodeBlob()
        .setGitRepo(merge(a, d, x -> x.GitRepo, x -> x.GitRepo, DiffMerger::mergeGitCode))
        .setPath(merge(a, d, x -> x.Path, x -> x.Path, DiffMerger::mergePathDatasetComponent));
  }

  public static ConfigBlob mergeConfig(ConfigBlob a, ConfigDiff d) {
    return new ConfigBlob()
        .setHyperparameters(
            mergeList(
                a,
                d,
                x -> x.Hyperparameters,
                x -> x.Hyperparameters,
                x -> x.Name,
                x -> x.Name,
                DiffMerger::mergeHyperparameterConfig))
        .setHyperparameterSet(
            mergeList(
                a,
                d,
                x -> x.HyperparameterSet,
                x -> x.HyperparameterSet,
                x -> x.Name,
                x -> x.Name,
                DiffMerger::mergeHyperparameterSetConfig));
  }

  public static HyperparameterConfigBlob mergeHyperparameterConfig(
      HyperparameterConfigBlob a, HyperparameterConfigDiff d) {
    return new HyperparameterConfigBlob()
        .setName(a != null ? a.Name : d.Name)
        .setValue(mergeLast(getOrNull(a, x -> x.Value), d, x -> x.B, x -> x.Status));
  }

  public static HyperparameterSetConfigBlob mergeHyperparameterSetConfig(
      HyperparameterSetConfigBlob a, HyperparameterSetConfigDiff d) {
    return new HyperparameterSetConfigBlob()
        .setName(a != null ? a.Name : d.Name)
        .setContinuous(
            mergeLast(getOrNull(a, x -> x.Continuous), d, x -> x.ContinuousB, x -> x.Status))
        .setDiscrete(mergeLast(getOrNull(a, x -> x.Discrete), d, x -> x.DiscreteB, x -> x.Status));
  }

  public static DatasetBlob mergeDataset(DatasetBlob a, DatasetDiff d) {
    return new DatasetBlob()
        .setPath(merge(a, d, x -> x.Path, x -> x.Path, DiffMerger::mergePathDataset))
        .setS3(merge(a, d, x -> x.S3, x -> x.S3, DiffMerger::mergeS3Dataset));
  }

  public static PathDatasetBlob mergePathDataset(PathDatasetBlob a, PathDatasetDiff d) {
    return new PathDatasetBlob()
        .setComponents(
            mergeList(
                a,
                d,
                x -> x.Components,
                x -> x.Components,
                x -> x.Path,
                x -> getOrNull(x.B, y -> y.Path),
                DiffMerger::mergePathDatasetComponent));
  }

  public static PathDatasetComponentBlob mergePathDatasetComponent(
      PathDatasetComponentBlob a, PathDatasetComponentDiff d) {
    return mergeLast(a, d, x -> x.B, x -> x.Status);
  }

  public static S3DatasetBlob mergeS3Dataset(S3DatasetBlob a, S3DatasetDiff d) {
    return new S3DatasetBlob()
        .setComponents(
            mergeList(
                a,
                d,
                x -> x.Components,
                x -> x.Components,
                x -> x.Path.Path,
                x -> getOrNull(x.Path.B, y -> y.Path),
                DiffMerger::mergeS3DatasetComponent));
  }

  public static S3DatasetComponentBlob mergeS3DatasetComponent(
      S3DatasetComponentBlob a, S3DatasetComponentDiff d) {
    return new S3DatasetComponentBlob()
        .setPath(merge(a, d, x -> x.Path, x -> x.Path, DiffMerger::mergePathDatasetComponent));
  }

  public static EnvironmentBlob mergeEnvironment(EnvironmentBlob a, EnvironmentDiff d) {
    return new EnvironmentBlob()
        .setPython(merge(a, d, x -> x.Python, x -> x.Python, DiffMerger::mergePythonEnvironment))
        .setDocker(merge(a, d, x -> x.Docker, x -> x.Docker, DiffMerger::mergeDockerEnvironment))
        .setEnvironmentVariables(
            mergeList(
                a,
                d,
                x -> x.EnvironmentVariables,
                x -> x.EnvironmentVariables,
                x -> x.Name,
                x -> x.Name,
                DiffMerger::mergeEnvironmentVariables))
        .setCommandLine(
            mergeLast(
                getOrNull(a, x -> x.CommandLine),
                d,
                x -> x.CommandLineB,
                x -> x.CommandLineStatus));
  }

  public static PythonEnvironmentBlob mergePythonEnvironment(
      PythonEnvironmentBlob a, PythonEnvironmentDiff d) {
    return new PythonEnvironmentBlob()
        .setVersion(
            mergeLast(getOrNull(a, x -> x.Version), d, x -> x.VersionB, x -> x.VersionStatus))
        .setConstraints(
            mergeList(
                a,
                d,
                x -> x.Constraints,
                x -> x.Constraints,
                x -> x.Library,
                x -> getOrNull(x.B, y -> y.Library),
                DiffMerger::mergePythonRequirementEnvironment))
        .setConstraints(
            mergeList(
                a,
                d,
                x -> x.Requirements,
                x -> x.Requirements,
                x -> x.Library,
                x -> getOrNull(x.B, y -> y.Library),
                DiffMerger::mergePythonRequirementEnvironment));
  }

  public static PythonRequirementEnvironmentBlob mergePythonRequirementEnvironment(
      PythonRequirementEnvironmentBlob a, PythonRequirementEnvironmentDiff d) {
    return mergeLast(a, d, x -> x.B, x -> x.Status);
  }

  public static DockerEnvironmentBlob mergeDockerEnvironment(
      DockerEnvironmentBlob a, DockerEnvironmentDiff d) {
    return mergeLast(a, d, x -> x.B, x -> x.Status);
  }

  public static EnvironmentVariablesBlob mergeEnvironmentVariables(
      EnvironmentVariablesBlob a, EnvironmentVariablesDiff d) {
    return new EnvironmentVariablesBlob()
        .setName(a != null ? a.Name : d.Name)
        .setValue(mergeLast(getOrNull(a, x -> x.Value), d, x -> x.ValueB, x -> x.Status));
  }
}
