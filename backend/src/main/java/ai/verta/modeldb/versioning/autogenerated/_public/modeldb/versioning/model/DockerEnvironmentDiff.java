// THIS FILE IS AUTO-GENERATED. DO NOT EDIT
package ai.verta.modeldb.versioning.autogenerated._public.modeldb.versioning.model;

import ai.verta.modeldb.ModelDBException;
import ai.verta.modeldb.versioning.*;
import ai.verta.modeldb.versioning.blob.visitors.Visitor;
import java.util.*;
import java.util.function.Function;

public class DockerEnvironmentDiff {
  public DiffStatusEnumDiffStatus Status;
  public DockerEnvironmentBlob A;
  public DockerEnvironmentBlob B;

  public DockerEnvironmentDiff() {
    this.Status = null;
    this.A = null;
    this.B = null;
  }

  public DockerEnvironmentDiff setStatus(DiffStatusEnumDiffStatus value) {
    this.Status = value;
    return this;
  }

  public DockerEnvironmentDiff setA(DockerEnvironmentBlob value) {
    this.A = value;
    return this;
  }

  public DockerEnvironmentDiff setB(DockerEnvironmentBlob value) {
    this.B = value;
    return this;
  }

  public static DockerEnvironmentDiff fromProto(
      ai.verta.modeldb.versioning.DockerEnvironmentDiff blob) {
    if (blob == null) {
      return null;
    }

    DockerEnvironmentDiff obj = new DockerEnvironmentDiff();
    {
      Function<ai.verta.modeldb.versioning.DockerEnvironmentDiff, DiffStatusEnumDiffStatus> f =
          x -> DiffStatusEnumDiffStatus.fromProto(blob.getStatus());
      obj.Status = f.apply(blob);
    }
    {
      Function<ai.verta.modeldb.versioning.DockerEnvironmentDiff, DockerEnvironmentBlob> f =
          x -> DockerEnvironmentBlob.fromProto(blob.getA());
      obj.A = f.apply(blob);
    }
    {
      Function<ai.verta.modeldb.versioning.DockerEnvironmentDiff, DockerEnvironmentBlob> f =
          x -> DockerEnvironmentBlob.fromProto(blob.getB());
      obj.B = f.apply(blob);
    }
    return obj;
  }

  public ai.verta.modeldb.versioning.DockerEnvironmentDiff.Builder toProto() {
    ai.verta.modeldb.versioning.DockerEnvironmentDiff.Builder builder =
        ai.verta.modeldb.versioning.DockerEnvironmentDiff.newBuilder();
    {
      if (this.Status != null) {
        Function<ai.verta.modeldb.versioning.DockerEnvironmentDiff.Builder, Void> f =
            x -> {
              builder.setStatus(this.Status.toProto());
              return null;
            };
        f.apply(builder);
      }
    }
    {
      if (this.A != null) {
        Function<ai.verta.modeldb.versioning.DockerEnvironmentDiff.Builder, Void> f =
            x -> {
              builder.setA(this.A.toProto());
              return null;
            };
        f.apply(builder);
      }
    }
    {
      if (this.B != null) {
        Function<ai.verta.modeldb.versioning.DockerEnvironmentDiff.Builder, Void> f =
            x -> {
              builder.setB(this.B.toProto());
              return null;
            };
        f.apply(builder);
      }
    }
    return builder;
  }

  public void preVisitShallow(Visitor visitor) throws ModelDBException {
    visitor.preVisitDockerEnvironmentDiff(this);
  }

  public void preVisitDeep(Visitor visitor) throws ModelDBException {
    this.preVisitShallow(visitor);
    visitor.preVisitDeepDiffStatusEnumDiffStatus(this.Status);
    visitor.preVisitDeepDockerEnvironmentBlob(this.A);
    visitor.preVisitDeepDockerEnvironmentBlob(this.B);
  }

  public DockerEnvironmentDiff postVisitShallow(Visitor visitor) throws ModelDBException {
    return visitor.postVisitDockerEnvironmentDiff(this);
  }

  public DockerEnvironmentDiff postVisitDeep(Visitor visitor) throws ModelDBException {
    this.Status = visitor.postVisitDeepDiffStatusEnumDiffStatus(this.Status);
    this.A = visitor.postVisitDeepDockerEnvironmentBlob(this.A);
    this.B = visitor.postVisitDeepDockerEnvironmentBlob(this.B);
    return this.postVisitShallow(visitor);
  }
}
