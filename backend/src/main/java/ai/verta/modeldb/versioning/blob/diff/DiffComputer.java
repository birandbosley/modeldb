package ai.verta.modeldb.versioning.blob.diff;

import ai.verta.modeldb.versioning.DiffStatusEnum.DiffStatus;
import ai.verta.modeldb.versioning.autogenerated._public.modeldb.versioning.model.*;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// TODO: autogenerate?
public class DiffComputer {
  public static <T, T2> T2 getOrNull(T v, Function<T, T2> getter) {
    if (v == null) {
      return null;
    }
    return getter.apply(v);
  }

  public static <B, F, R> R computeDiff(
      B a, B b, Function<B, F> getter, Function3<F, F, R> computer) {
    F newA = getOrNull(a, getter);
    F newB = getOrNull(b, getter);
    if (newA == null && newB == null) {
      return null;
    }
    return computer.apply(newA, newB);
  }

  // This applies an algorithm similar to what I discussed with Ravi for merges: gather sets based
  // on a key
  // 1. if there is a key collision and both sets have size 1, then compute the diff of those
  // elements
  // 2. if there is a key collision and both sets have more than 1 element, consider all A as
  // removal and B as addition, ignoring any modifications (by passing null values)
  // 3. if there is no key collision, then just process the right side
  public static <B, F, R> List<R> computeListDiff(
      B a,
      B b,
      Function<B, List<F>> getter,
      Function<F, String> hasher,
      Function3<F, F, R> computer) {
    Map<String, HashSet<F>> mapA = toMap(getter.apply(a), hasher);
    Map<String, HashSet<F>> mapB = toMap(getter.apply(b), hasher);

    HashSet<String> keys = new HashSet<>();
    keys.addAll(mapA.keySet());
    keys.addAll(mapB.keySet());

    return keys.stream()
        .flatMap(
            key -> {
              HashSet<F> elA = mapA.get(key);
              HashSet<F> elB = mapB.get(key);
              // Key collision and one element, process it
              if (elA != null && elB != null && elA.size() == 1 && elB.size() == 1) {
                final F nextA = elA.iterator().next();
                final F nextB = elB.iterator().next();
                if (nextA.equals(nextB)) {
                  return null;
                }
                return Stream.of(computer.apply(nextA, nextB));
              }

              // Key collision and more elements, consider removal + addition
              if (elA != null && elB != null) {
                return Stream.concat(
                    elA.stream().map(el -> computer.apply(el, null)),
                    elB.stream().map(el -> computer.apply(null, el)));
              } else if (elA != null) {
                return elA.stream().map(el -> computer.apply(el, null));
              } else {
                return elB.stream().map(el -> computer.apply(null, el));
              }
            })
        .collect(Collectors.toList());
  }

  private static <F> Map<String, HashSet<F>> toMap(List<F> input, Function<F, String> hasher) {
    return input.stream()
        .collect(
            Collectors.toMap(
                hasher,
                el -> new HashSet<>(Collections.singletonList(el)),
                (entry1, entry2) -> {
                  HashSet<F> result2 = new HashSet<>(entry1);
                  result2.addAll(entry2);
                  return result2;
                }));
  }

  public static <T> DiffStatusEnumDiffStatus getStatus(T a, T b) {
    return DiffStatusEnumDiffStatus.fromProto(status(a, b));
  }

  private static <T> DiffStatus status(T a, T b) {
    if (a == null && b == null) {
      return DiffStatus.DELETED;
    }
    if (a == null) {
      return DiffStatus.ADDED;
    }
    if (b == null) {
      return DiffStatus.DELETED;
    }
    return DiffStatus.MODIFIED;
  }

  public static BlobDiff computeBlobDiff(Blob a, Blob b) {
    return new BlobDiff()
        .setCode(computeDiff(a, b, x -> x.Code, DiffComputer::computeCodeDiff))
        .setConfig(computeDiff(a, b, x -> x.Config, DiffComputer::computeConfigDiff))
        .setDataset(computeDiff(a, b, x -> x.Dataset, DiffComputer::computeDatasetDiff))
        .setEnvironment(
            computeDiff(a, b, x -> x.Environment, DiffComputer::computeEnvironmentDiff));
  }

  public static CodeDiff computeCodeDiff(CodeBlob a, CodeBlob b) {
    return new CodeDiff()
        .setGit(computeDiff(a, b, x -> x.Git, DiffComputer::computeGitCodeDiff))
        .setNotebook(computeDiff(a, b, x -> x.Notebook, DiffComputer::computeNotebookCodeDiff));
  }

  public static GitCodeDiff computeGitCodeDiff(GitCodeBlob a, GitCodeBlob b) {
    return new GitCodeDiff().setA(a).setB(b).setStatus(getStatus(a, b));
  }

  public static NotebookCodeDiff computeNotebookCodeDiff(NotebookCodeBlob a, NotebookCodeBlob b) {
    return new NotebookCodeDiff()
        .setGitRepo(computeDiff(a, b, x -> x.GitRepo, DiffComputer::computeGitCodeDiff))
        .setPath(computeDiff(a, b, x -> x.Path, DiffComputer::computePathDatasetComponentDiff));
  }

  public static ConfigDiff computeConfigDiff(ConfigBlob a, ConfigBlob b) {
    return new ConfigDiff()
        .setHyperparameters(
            computeListDiff(
                a,
                b,
                x -> x == null ? Collections.emptyList() : x.Hyperparameters,
                x -> x.Name,
                DiffComputer::computeHyperparameterConfigDiff))
        .setHyperparameterSet(
            computeListDiff(
                a,
                b,
                x -> x == null ? Collections.emptyList() : x.HyperparameterSet,
                x -> x.Name,
                DiffComputer::computeHyperparameterSetConfigDiff));
  }

  public static HyperparameterConfigDiff computeHyperparameterConfigDiff(
      HyperparameterConfigBlob a, HyperparameterConfigBlob b) {
    String name = a != null ? a.Name : b.Name;
    return new HyperparameterConfigDiff()
        .setName(name)
        .setA(getOrNull(a, x -> x.Value))
        .setB(getOrNull(b, x -> x.Value))
        .setStatus(getStatus(a, b));
  }

  public static HyperparameterSetConfigDiff computeHyperparameterSetConfigDiff(
      HyperparameterSetConfigBlob a, HyperparameterSetConfigBlob b) {
    String name = a != null ? a.Name : b.Name;
    return new HyperparameterSetConfigDiff()
        .setName(name)
        .setContinuousA(getOrNull(a, x -> x.Continuous))
        .setContinuousB(getOrNull(b, x -> x.Continuous))
        .setDiscreteA(getOrNull(a, x -> x.Discrete))
        .setDiscreteB(getOrNull(b, x -> x.Discrete))
        .setStatus(getStatus(a, b));
  }

  public static DatasetDiff computeDatasetDiff(DatasetBlob a, DatasetBlob b) {
    return new DatasetDiff()
        .setPath(computeDiff(a, b, x -> x.Path, DiffComputer::computePathDatasetDiff))
        .setS3(computeDiff(a, b, x -> x.S3, DiffComputer::computeS3DatasetDiff));
  }

  public static PathDatasetDiff computePathDatasetDiff(PathDatasetBlob a, PathDatasetBlob b) {
    return new PathDatasetDiff()
        .setComponents(
            computeListDiff(
                a,
                b,
                x -> x == null ? Collections.emptyList() : x.Components,
                x -> x.Path,
                DiffComputer::computePathDatasetComponentDiff));
  }

  public static PathDatasetComponentDiff computePathDatasetComponentDiff(
      PathDatasetComponentBlob a, PathDatasetComponentBlob b) {
    return new PathDatasetComponentDiff().setA(a).setB(b).setStatus(getStatus(a, b));
  }

  public static S3DatasetDiff computeS3DatasetDiff(S3DatasetBlob a, S3DatasetBlob b) {
    return new S3DatasetDiff()
        .setComponents(
            computeListDiff(
                a,
                b,
                x -> x == null ? Collections.emptyList() : x.Components,
                x -> x.Path.Path,
                DiffComputer::computeS3DatasetComponentDiff));
  }

  public static S3DatasetComponentDiff computeS3DatasetComponentDiff(
      S3DatasetComponentBlob a, S3DatasetComponentBlob b) {
    return new S3DatasetComponentDiff()
        .setPath(computeDiff(a, b, x -> x.Path, DiffComputer::computePathDatasetComponentDiff));
  }

  public static EnvironmentDiff computeEnvironmentDiff(EnvironmentBlob a, EnvironmentBlob b) {
    return new EnvironmentDiff()
        .setCommandLineA(getOrNull(a, x -> x.CommandLine))
        .setCommandLineB(getOrNull(b, x -> x.CommandLine))
        .setCommandLineStatus(
            getStatus(getOrNull(a, x -> x.CommandLine), getOrNull(b, x -> x.CommandLine)))
        .setDocker(computeDiff(a, b, x -> x.Docker, DiffComputer::computeDockerEnvironmentDiff))
        .setPython(computeDiff(a, b, x -> x.Python, DiffComputer::computePythonEnvironmentDiff))
        .setEnvironmentVariables(
            computeListDiff(
                a,
                b,
                x -> x == null ? Collections.emptyList() : x.EnvironmentVariables,
                x -> x.Name,
                DiffComputer::computeEnvironmentVariablesDiff));
  }

  public static DockerEnvironmentDiff computeDockerEnvironmentDiff(
      DockerEnvironmentBlob a, DockerEnvironmentBlob b) {
    return new DockerEnvironmentDiff().setA(a).setB(b).setStatus(getStatus(a, b));
  }

  public static PythonEnvironmentDiff computePythonEnvironmentDiff(
      PythonEnvironmentBlob a, PythonEnvironmentBlob b) {
    return new PythonEnvironmentDiff()
        .setVersionA(getOrNull(a, x -> x.Version))
        .setVersionB(getOrNull(b, x -> x.Version))
        .setVersionStatus(getStatus(getOrNull(a, x -> x.Version), getOrNull(b, x -> x.Version)))
        .setConstraints(
            computeListDiff(
                a,
                b,
                x -> x == null ? Collections.emptyList() : x.Constraints,
                x -> x.Library,
                DiffComputer::computePythonRequirementEnvironmentDiff))
        .setRequirements(
            computeListDiff(
                a,
                b,
                x -> x == null ? Collections.emptyList() : x.Requirements,
                x -> x.Library,
                DiffComputer::computePythonRequirementEnvironmentDiff));
  }

  public static PythonRequirementEnvironmentDiff computePythonRequirementEnvironmentDiff(
      PythonRequirementEnvironmentBlob a, PythonRequirementEnvironmentBlob b) {
    return new PythonRequirementEnvironmentDiff().setA(a).setB(b).setStatus(getStatus(a, b));
  }

  public static EnvironmentVariablesDiff computeEnvironmentVariablesDiff(
      EnvironmentVariablesBlob a, EnvironmentVariablesBlob b) {
    String name = a != null ? a.Name : b.Name;
    return new EnvironmentVariablesDiff()
        .setName(name)
        .setValueA(getOrNull(a, x -> x.Value))
        .setValueB(getOrNull(b, x -> x.Value))
        .setStatus(getStatus(a, b));
  }
}
