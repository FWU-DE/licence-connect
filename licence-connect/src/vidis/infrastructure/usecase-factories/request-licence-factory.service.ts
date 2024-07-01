import { Injectable } from '@nestjs/common';
import {
  RequestLicencesForStudentUseCase,
  createRequestLicencesForStudentUseCase,
} from '@vidis/usecases/request-licences-use-case';
import { InMemoryRepositoryService } from '../../../licences/infrastructure/repository/in-memory-repository.service';
import { BundeslandIdentificationString } from '@licences/domain/ferderal-state-id';

@Injectable()
export class RequestLicenceFactoryService {
  constructor(private readonly licenceRepository: InMemoryRepositoryService) {}

  public createLicenceRequestUseCase(
    _ferderalStateIdentifier: BundeslandIdentificationString,
    _schoolIdentifier: string,
    _applicationId: string,
  ): RequestLicencesForStudentUseCase {
    return createRequestLicencesForStudentUseCase(this.licenceRepository);
  }
}
