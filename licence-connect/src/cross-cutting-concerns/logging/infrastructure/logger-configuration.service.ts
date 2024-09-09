import { Injectable } from '@nestjs/common';
import { ConfigService } from '@nestjs/config';
import { LoggerConfig } from '../domain/logger-config';
import { LoggingLevel } from '../domain/logging-level';

const DEFAULT_LOG_LEVEL: LoggingLevel = 'info';
const DEFAULT_LOG_PATH: string = 'log';
const DEFAULT_LOG_File: string = 'log.licenceconnect.txt';

@Injectable()
export class LoggerConfigurationService {
  constructor(private readonly nestConfigurationService: ConfigService) {}

  public getLoggingConfiguration(): LoggerConfig {
    return {
      loggingLevel: this.getEnvironmentVariableOrDefault<LoggingLevel>(
        'LOG_LEVEL',
        DEFAULT_LOG_LEVEL,
      ),
      loggingPath: this.getEnvironmentVariableOrDefault<string>(
        'LOG_PATH',
        DEFAULT_LOG_PATH,
      ),
      loggingFile: this.getEnvironmentVariableOrDefault<string>(
        'LOG_FILE',
        DEFAULT_LOG_File,
      ),
    };
  }

  private getEnvironmentVariableOrDefault<T extends string>(
    environment_variable_key: string,
    default_value: T,
  ) {
    return (
      this.nestConfigurationService.get<string>(environment_variable_key) ??
      default_value
    );
  }
}
