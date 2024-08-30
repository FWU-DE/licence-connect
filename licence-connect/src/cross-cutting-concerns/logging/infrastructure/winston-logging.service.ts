import { Inject, Injectable } from '@nestjs/common';
import { LCLogger } from '../domain/logger';
import { WINSTON_MODULE_PROVIDER } from 'nest-winston';
import { Logger } from 'winston';

@Injectable()
export class WinstonLoggingService implements LCLogger {
  constructor(
    @Inject(WINSTON_MODULE_PROVIDER) private readonly winstonLogger: Logger,
  ) {}
  debug(debugOutput: string): void {
    this.winstonLogger.debug(debugOutput);
  }
  info(infoOutput: string): void {
    this.winstonLogger.info(infoOutput);
  }
  warn(warnOutput: string): void {
    this.winstonLogger.warn(warnOutput);
  }
  error(errorOutput: string): void {
    this.winstonLogger.error(errorOutput);
  }
}
