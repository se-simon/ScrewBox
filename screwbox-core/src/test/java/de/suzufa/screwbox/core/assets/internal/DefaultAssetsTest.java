package de.suzufa.screwbox.core.assets.internal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import de.suzufa.screwbox.core.assets.Asset;
import de.suzufa.screwbox.core.async.Async;
import de.suzufa.screwbox.core.log.Log;

@ExtendWith(MockitoExtension.class)
class DefaultAssetsTest {

    @Mock
    Async async;

    @Mock
    Log log;

    @InjectMocks
    DefaultAssets assets;

    private static final Asset<String> ASSET_A = Asset.asset(() -> "loaded");
    private static final Asset<String> ASSET_B = Asset.asset(() -> "loaded");
    private static final Asset<String> ASSET_C = Asset.asset(() -> "loaded");

    @Test
    void listAssetLocationsInPackage_packageDoesntExist_emptyList() {
        var locations = assets.listAssetLocationsInPackage("de.suzufa.unknown");

        assertThat(locations).isEmpty();
    }

    @Test
    void listAssetLocationsInPackage_noAssetsInPackage_emptyList() {
        var locations = assets.listAssetLocationsInPackage("de.suzufa.core.audio");

        assertThat(locations).isEmpty();
    }

    @Test
    void listAssetLocationsInPackage_packageExists_listsLocations() {
        var locations = assets.listAssetLocationsInPackage("de.suzufa.screwbox.core.assets.internal");

        assertThat(locations).hasSize(3)
                .allMatch(asset -> asset.sourceClass().equals(DefaultAssetsTest.class))
                .anyMatch(asset -> asset.asset().equals(ASSET_A))
                .anyMatch(asset -> asset.sourceField().getName().equals("ASSET_A"))
                .anyMatch(asset -> asset.asset().equals(ASSET_B))
                .anyMatch(asset -> asset.sourceField().getName().equals("ASSET_B"))
                .anyMatch(asset -> asset.asset().equals(ASSET_C))
                .anyMatch(asset -> asset.sourceField().getName().equals("ASSET_C"));
    }

    @Test
    void preparePackage_assetsUnloadedEnabledLog_loadsAssetsAndLogs() {
        ASSET_C.load();

        assertThat(ASSET_A.isLoaded()).isFalse();
        assertThat(ASSET_B.isLoaded()).isFalse();

        assets.enableLogging();
        var prepared = assets.preparePackage("de.suzufa.screwbox.core.assets.internal");

        assertThat(ASSET_A.isLoaded()).isTrue();
        assertThat(ASSET_B.isLoaded()).isTrue();

        var logMessage = ArgumentCaptor.forClass(String.class);
        verify(log).debug(logMessage.capture());
        assertThat(logMessage.getValue()).startsWith("loaded 2 assets in ").endsWith(" ms");

        assertThat(prepared).hasSize(2)
                .anyMatch(asset -> asset.asset().equals(ASSET_A))
                .anyMatch(asset -> asset.asset().equals(ASSET_B));
    }

    @Test
    void preparePackage_loggingDisabled_doesntLog() {
        assets.preparePackage("de.suzufa.screwbox.core.assets.internal");

        verify(log, never()).debug(anyString());
    }

}
